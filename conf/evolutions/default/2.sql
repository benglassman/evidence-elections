# elections schema

# --- !Ups
CREATE TABLE if not exists state(

stateID int NOT NULL AUTO_INCREMENT,
stateCode nchar(2) NOT NULL,
stateName nvarchar(128) NOT NULL,
primary key(stateID)
);

# --- !Downs
drop table state;
