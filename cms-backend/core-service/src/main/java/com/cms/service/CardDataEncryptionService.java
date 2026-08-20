package com.cms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PCI DSS: Encrypts/decrypts cardholder data (PAN, CVV, etc.) at rest.
 * Uses AES-256-GCM. Key from config (cms.card.encryption-key or CARD_ENCRYPTION_KEY env).
 */
@Service
public class CardDataEncryptionService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKey key;
    private final String hashSalt;

    public CardDataEncryptionService(
            @Value("${cms.card.encryption-key:}") String encryptionKey,
            @Value("${cms.card.encryption-key-env:CARD_ENCRYPTION_KEY}") String envKeyName,
            @Value("${cms.card.hash-salt:}") String hashSalt,
            @Value("${cms.card.encryption-optional:true}") boolean optional) {
        String raw = encryptionKey != null && !encryptionKey.isBlank()
                ? encryptionKey
                : System.getenv(envKeyName);
        if (raw == null || raw.isBlank()) {
            if (optional) {
                raw = "0000000000000000000000000000000000000000000000000000000000000000";
            } else {
                throw new IllegalStateException(
                        "Card encryption key is required. Set cms.card.encryption-key or " + envKeyName + " environment variable.");
            }
        }
        byte[] keyBytes = decodeKey(raw);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("Card encryption key must be 256 bits (32 bytes). Use hex (64 chars) or base64.");
        }
        this.key = new SecretKeySpec(keyBytes, "AES");
        this.hashSalt = hashSalt != null ? hashSalt : "";
    }

    private static byte[] decodeKey(String raw) {
        raw = raw.trim();
        if (raw.length() == 64 && raw.matches("[0-9a-fA-F]+")) {
            byte[] b = new byte[32];
            for (int i = 0; i < 32; i++) {
                b[i] = (byte) Integer.parseInt(raw.substring(i * 2, i * 2 + 2), 16);
            }
            return b;
        }
        try {
            return Base64.getDecoder().decode(raw);
        } catch (IllegalArgumentException e) {
            return raw.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Encrypts plaintext (e.g. PAN). Returns base64(IV || ciphertext || tag).
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) return null;
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.allocate(iv.length + ciphertext.length);
            buf.put(iv);
            buf.put(ciphertext);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (java.security.GeneralSecurityException e) {
            throw new RuntimeException("Card data encryption failed", e);
        }
    }

    /**
     * Decrypts a value produced by {@link #encrypt(String)}.
     */
    public String decrypt(String encrypted) {
        if (encrypted == null || encrypted.isBlank()) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            if (decoded.length < GCM_IV_LENGTH + 16) {
                throw new IllegalArgumentException("Invalid encrypted payload");
            }
            ByteBuffer buf = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buf.get(iv);
            byte[] ciphertext = new byte[buf.remaining()];
            buf.get(ciphertext);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] plain = cipher.doFinal(ciphertext);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | java.security.GeneralSecurityException e) {
            throw new RuntimeException("Card data decryption failed", e);
        }
    }

    /**
     * Decrypts CVV/track (or similar) stored at rest.
     * Prefers AES-GCM ({@link #encrypt}); falls back to legacy plain Base64 for older rows.
     */
    public String decryptSensitiveField(String stored) {
        if (stored == null || stored.isBlank()) return null;
        try {
            return decrypt(stored);
        } catch (RuntimeException aesFailed) {
            try {
                return new String(Base64.getDecoder().decode(stored), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException legacyFailed) {
                throw aesFailed;
            }
        }
    }
//
//    /**
//     * Mask PAN for display: last 4 digits only (PCI DSS).
//     */
//    public String maskPan(String pan) {
//        if (pan == null || pan.length() < 4) return "****";
//        return "****" + pan.substring(pan.length() - 4);
//    }

    /** Mask PAN for display: show first 6 and last 4 digits. */
    public String maskPan(String pan) {
        if (pan == null || pan.isBlank()) return "****";
        if (pan.contains("*")) return pan;
        if (pan.length() <= 10) return "****";
        return pan.substring(0, 6)
                + "*".repeat(pan.length() - 10)
                + pan.substring(pan.length() - 4);
    }



    /**
     * One-way hash for exact PAN lookup (e.g. find card by full PAN without storing plaintext).
     * Uses SHA-256( salt + pan ). Salt from config (cms.card.hash-salt).
     */
    public String panHashForLookup(String pan) {
        if (pan == null || pan.isBlank()) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((hashSalt + pan).getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("PAN hash failed", e);
        }
    }

    /**
     * Last 4 digits of PAN for search/display (stored in DB as non-sensitive).
     */
    public String panLast4(String pan) {
        if (pan == null || pan.length() < 4) return null;
        return pan.substring(pan.length() - 4);
    }
}
