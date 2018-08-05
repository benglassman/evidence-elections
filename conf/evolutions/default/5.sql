# elections schema

# --- !Ups
ALTER TABLE candidates
ADD COLUMN name VARCHAR(20) AFTER candidateid;
