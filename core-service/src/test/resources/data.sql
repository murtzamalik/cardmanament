-- Seed admin user for integration tests (H2). PCI DSS: use BCrypt hash for test123.
INSERT INTO USM_USER (LOGIN_ID, PASSWORD, FULL_NAME, IS_ACTIVE, APP_ID)
VALUES ('admin', '$2a$10$Ofm1VCFz.isWs0sbmH8BGec.1jzChf6nlt2UeABzqLCT6yoOTbuyC', 'Admin User', 1, 'CMS');
