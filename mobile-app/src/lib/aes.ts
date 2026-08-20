import CryptoJS from 'crypto-js';
import { AES_IV, AES_SECRET_KEY } from '../config/api';

/** Matches backend AESencryption.encryptwith256 (AES/CBC/PKCS5 + Base64). */
export function encryptPin(plainPin: string): string {
  const key = CryptoJS.enc.Utf8.parse(AES_SECRET_KEY);
  const iv = CryptoJS.enc.Utf8.parse(AES_IV);
  const encrypted = CryptoJS.AES.encrypt(plainPin, key, {
    iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7,
  });
  return encrypted.toString();
}
