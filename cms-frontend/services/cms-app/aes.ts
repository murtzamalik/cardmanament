import { CMS_APP_AES } from '@/lib/constants';

function bytesToBase64(bytes: Uint8Array): string {
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}

/** Matches cms-app AESencryption.encryptwith256 (AES/CBC/PKCS5 + Base64). */
export async function encryptPin(plainPin: string): Promise<string> {
  const keyBytes = new TextEncoder().encode(CMS_APP_AES.secretKey);
  const iv = new TextEncoder().encode(CMS_APP_AES.iv);
  const key = await crypto.subtle.importKey('raw', keyBytes, { name: 'AES-CBC' }, false, ['encrypt']);
  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-CBC', iv },
    key,
    new TextEncoder().encode(plainPin)
  );
  return bytesToBase64(new Uint8Array(encrypted));
}
