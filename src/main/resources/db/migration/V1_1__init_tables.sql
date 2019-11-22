-- NB Flyway requires lowercase for table names
create schema if not exists dbjuddata;

create table judicial_user(
	id uuid not null,
	personal_code varchar(255) not null,
	title varchar(255) not null,
	known_as varchar(255) not null,
	surname varchar(255) not null,
	full_name varchar(255) not null,
	post_nominals varchar(255),
	email varchar(255) not null,
	last_updated timestamp,
	created timestamp,
	constraint judicial_user_id_pk primary key (id),
	constraint personal_code unique (id)
);

create table judicial_office_appointments(
	id uuid not null,
	role_id varchar(255) not null,
	type_id varchar(255) not null,
	court_id varchar(255),
	court_type_id varchar(255) not null,
	circuit_id varchar(255) not null,
	bench_id varchar(255),
	area_id varchar(255) not null,
	location_id varchar(255),
	is_principal boolean,
	last_updated timestamp,
	created timestamp,
	jud_user_id uuid not null,
	constraint judicial_office_appointment_id_pk primary key (id),
	constraint role_id unique (id)
);

create table judicial_office_authorisations(
	id uuid not null,
	jurisdiction varchar(255) not null,
	ticket_id varchar(255) not null,
    start_date timestamp,
    end_date timestamp not null,
	last_updated timestamp,
	created timestamp,
	jud_user_id uuid not null,
	constraint judicial_office_authorisation_id_pk primary key (id),
	constraint jurisdiction unique (id)
);

alter table judicial_office_appointments add constraint judicial_user_id_fk1 foreign key (jud_user_id) references judicial_user (id);

alter table judicial_office_authorisations add constraint judicial_user_id_fk2 foreign key (jud_user_id) references judicial_user (id);