# elections schema

# --- !Ups
CREATE TABLE if not exists race (
  raceid BIGINT NOT NULL AUTO_INCREMENT,
  raceType VARCHAR(20) NOT NULL,
  state INT NOT NULL,
  candidate1id BIGINT NOT NULL,
  candidate2id BIGINT NOT NULL,
  PRIMARY KEY(raceid),
  FOREIGN KEY fk_state(state)
   REFERENCES state(stateid)
   ON UPDATE CASCADE
   ON DELETE RESTRICT
);



# --- !Downs
drop table race;