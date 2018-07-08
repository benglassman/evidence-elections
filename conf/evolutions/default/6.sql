# elections schema

# --- !Ups
CREATE TABLE if not exists video (
  id BIGINT NOT NULL AUTO_INCREMENT,
  raceId BIGINT NOT NULL,
  youTubeId VARCHAR(20) NOT NULL,
  candidateId BIGINT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY fk_race(raceId)
   REFERENCES race(raceid)
   ON UPDATE CASCADE
   ON DELETE RESTRICT,
  FOREIGN KEY fk_candidate(candidateId)
   REFERENCES candidates(candidateid)
   ON UPDATE CASCADE
   ON DELETE RESTRICT
);

# --- !Downs
drop table video;