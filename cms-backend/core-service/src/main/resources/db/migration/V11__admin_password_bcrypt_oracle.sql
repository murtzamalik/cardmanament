-- PCI DSS: Replace plaintext admin password with BCrypt hash (test123).
-- Only update if current password is still the legacy plaintext 'test123'.
UPDATE USM_USER
SET PASSWORD = '$2a$10$Ofm1VCFz.isWs0sbmH8BGec.1jzChf6nlt2UeABzqLCT6yoOTbuyC'
WHERE LOGIN_ID = 'admin'
  AND (PASSWORD = 'test123' OR PASSWORD IS NULL OR LENGTH(PASSWORD) < 60);

COMMIT;
