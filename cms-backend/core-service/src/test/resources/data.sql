-- Seed admin user for integration tests (H2). PCI DSS: use BCrypt hash for test123.
-- H2 does not reliably auto-populate Oracle sequence-backed IDs for raw SQL inserts,
-- so provide ID explicitly to avoid "NULL not allowed for column ID" failures.
INSERT INTO USM_USER (ID, LOGIN_ID, PASSWORD, FULL_NAME, IS_ACTIVE, APP_ID)
VALUES (1, 'admin', '$2a$10$Ofm1VCFz.isWs0sbmH8BGec.1jzChf6nlt2UeABzqLCT6yoOTbuyC', 'Admin User', 1, 'CMS');
