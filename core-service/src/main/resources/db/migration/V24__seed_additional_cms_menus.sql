-- V24: Seed additional menus used by dynamic RBAC navigation

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Menus', '/security/menus', 'pi-sitemap', 8, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/security/menus');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Branches', '/housekeeping/branches', 'pi-map-marker', 9, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/branches');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Account Statuses', '/housekeeping/account-statuses', 'pi-info-circle', 10, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/account-statuses');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Account Types', '/housekeeping/account-types', 'pi-list', 11, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/account-types');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Products', '/housekeeping/products', 'pi-box', 12, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/products');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Card Types', '/housekeeping/card-types', 'pi-credit-card', 13, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/card-types');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Limit Profiles', '/housekeeping/limit-profiles', 'pi-wallet', 14, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/limit-profiles');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Policies', '/housekeeping/policies', 'pi-file', 15, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/policies');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Response Codes', '/housekeeping/response-codes', 'pi-code', 16, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/housekeeping/response-codes');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Card Export', '/operations/cards/export', 'pi-download', 17, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/operations/cards/export');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Change Card Status', '/operations/cards/change-status', 'pi-flag', 18, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/operations/cards/change-status');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Replacement Request', '/operations/cards/replacement-request', 'pi-replay', 19, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/operations/cards/replacement-request');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Change Card Type', '/operations/cards/change-type', 'pi-sync', 20, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/operations/cards/change-type');

INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, MENU_ICON, SORT_ORDER, STATUS)
SELECT CMS_MENU_SEQ.NEXTVAL, 'Expiry Cards', '/operations/cards/expiry', 'pi-calendar', 21, 'Y'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CMS_MENU WHERE MENU_PATH = '/operations/cards/expiry');

COMMIT;
