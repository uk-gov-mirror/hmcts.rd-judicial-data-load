-- NB Flyway requires lowercase for table names
create schema if not exists dbjuddata;

create table judicial_user(
	sno varchar(255) not null,
	firstName varchar(255) not null,
	LastName varchar(255) not null,
	Circuit varchar(255) not null,
	Area varchar(255) not null
);

