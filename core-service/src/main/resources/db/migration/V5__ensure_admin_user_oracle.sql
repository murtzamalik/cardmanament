-- Ensure default admin user exists for login (admin / test123).
-- PCI DSS: Store BCrypt hash, not plaintext. Same hash as V11 migration.
INSERT INTO USM_USER (LOGIN_ID, PASSWORD, FULL_NAME, IS_ACTIVE, CREATED_ON, CREATED_BY, UPDATED_ON, UPDATED_BY, APP_ID)
SELECT 'admin', '$2a$10$Ofm1VCFz.isWs0sbmH8BGec.1jzChf6nlt2UeABzqLCT6yoOTbuyC', 'Admin User', 1, SYSTIMESTAMP, 'system', SYSTIMESTAMP, 'system', 'CMS'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USM_USER WHERE LOGIN_ID = 'admin');

COMMIT;
