-- V30: Link change-type / replacement requests to the source card.
-- Used on approve/generate to mark the old card Hot (003) and issue the new card Warm (002).

BEGIN
  EXECUTE IMMEDIATE 'ALTER TABLE CARD_REQUEST ADD SOURCE_CARD_ID NUMBER(19)';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE != -1430 THEN RAISE; END IF; -- ORA-01430: column already exists
END;
/
