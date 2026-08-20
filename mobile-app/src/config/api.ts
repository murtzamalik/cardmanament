/** Production/test API — deployed cms-app */
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.trim() || 'http://46.224.146.158:8016';

export const AES_SECRET_KEY = 'ais-567890123456789012345678-256';
export const AES_IV = '1234567890123456';
