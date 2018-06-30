# elections schema

# --- !Ups
CREATE TABLE if not exists user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(20) NOT NULL,
  password VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO user (user_name, password)
VALUES ('stest','$2a$10$niF.amAexQMHaevqlkganeSjvMHfTq/OdISyj8/5BQy1FHvlbi3Ne')

# --- !Downs
drop table user;