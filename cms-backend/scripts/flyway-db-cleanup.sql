-- =============================================================================
-- Flyway cleanup for CMS schema (Oracle user: CMS)
-- Run BEFORE starting the app with spring.flyway.enabled=true
--
-- Problem: flyway_schema_history has a failed V2 (tables already exist).
--          Flyway blocks startup and/or re-runs V2 → ORA-00955.
-- Goal:    Align history with the real schema, then let Flyway apply V15+.
-- =============================================================================

-- 1) Inspect current Flyway history
SELECT installed_rank, version, description, type, success, installed_on, script
FROM flyway_schema_history
ORDER BY installed_rank;

-- 2) Check login-related gaps (should be missing before cleanup + migrate)
SELECT column_name
FROM user_tab_columns
WHERE table_name = 'USM_USER' AND column_name = 'ID';

SELECT sequence_name
FROM user_sequences
WHERE sequence_name IN ('USM_USER_SEQ', 'AUDIT_LOG_SEQ');

-- 3) Sanity: core tables from V2+ should already exist
SELECT table_name
FROM user_tables
WHERE table_name IN (
  'USM_APPLICATION', 'USM_USER', 'USM_GROUP', 'BIZ_PROCESS',
  'CARD_ACCOUNT', 'LIMIT_PROFILE'
)
ORDER BY table_name;

-- =============================================================================
-- FIX (pick ONE path after reviewing step 1–3)
-- =============================================================================

-- PATH A (recommended): Baseline at V14 when schema matches V1–V14 but history is broken.
-- USM_USER must NOT have ID column yet (V15 adds it).
-- Uncomment and run only if the checks above look correct:

/*
DELETE FROM flyway_schema_history;

INSERT INTO flyway_schema_history (
    installed_rank, version, description, type, script,
    checksum, installed_by, installed_on, execution_time, success
) VALUES (
    1, '14', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>',
    NULL, USER, SYSTIMESTAMP, 0, 1
);

COMMIT;
*/

-- PATH B (minimal): Remove only failed rows, then mark V2 success if USM_APPLICATION exists.
-- Use when V1 is success and only V2 failed; V3–V14 were never recorded.

/*
DELETE FROM flyway_schema_history WHERE success = 0;
COMMIT;

-- After DELETE, if USM_APPLICATION exists, insert V2 as already applied:
INSERT INTO flyway_schema_history (
    installed_rank, version, description, type, script,
    checksum, installed_by, installed_on, execution_time, success
) VALUES (
    2, '2', 'add usm and ref tables oracle', 'SQL',
    'V2__add_usm_and_ref_tables_oracle.sql',
    NULL, USER, SYSTIMESTAMP, 0, 1
);
COMMIT;

-- Repeat for versions 3–14 if those objects exist but are not in history.
-- Easier: use PATH A instead.
*/

-- =============================================================================
-- After cleanup: start the app. Flyway should run V15 … V26 (repair runs on startup).
-- Verify:
-- =============================================================================
-- SELECT version, success FROM flyway_schema_history ORDER BY installed_rank;
-- SELECT column_name FROM user_tab_columns WHERE table_name = 'USM_USER' AND column_name = 'ID';
-- SELECT sequence_name FROM user_sequences WHERE sequence_name = 'AUDIT_LOG_SEQ';
