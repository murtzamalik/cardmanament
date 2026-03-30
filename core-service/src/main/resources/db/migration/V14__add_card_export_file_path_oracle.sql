-- Store path to bureau/export file when generated at card creation (approve-and-generate)
ALTER TABLE CARD ADD EXPORT_FILE_PATH VARCHAR2(1024) NULL;
