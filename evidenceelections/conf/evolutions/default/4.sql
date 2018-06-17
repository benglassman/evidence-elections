# elections schema

# --- !Ups
CREATE TABLE if not exists candidates (
  candidateid BIGINT NOT NULL AUTO_INCREMENT,
  party VARCHAR(20) NOT NULL,
  PRIMARY KEY(candidateid)
);

# --- !Downs
drop table candidates;