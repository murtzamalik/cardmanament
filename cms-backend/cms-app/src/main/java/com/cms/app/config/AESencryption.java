package com.cms.app.config;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESencryption {

    private final byte[] ivParameter = JwtConstants.IV_PARAM.getBytes(StandardCharsets.UTF_8);

    @SuppressWarnings(JwtConstants.JWT_SUPRESS_WARNING)
    public String encryptwith256(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = generateSecretKey(JwtConstants.AES_SECRET_KEY);
            Cipher cipher = Cipher.getInstance(JwtConstants.PADDING_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivParameter));
            byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings(JwtConstants.JWT_SUPRESS_WARNING)
    public String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = generateSecretKey(JwtConstants.AES_SECRET_KEY);
            Cipher cipher = Cipher.getInstance(JwtConstants.PADDING_CBC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivParameter));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKeySpec generateSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, JwtConstants.ALGORITHM);
    }
}
