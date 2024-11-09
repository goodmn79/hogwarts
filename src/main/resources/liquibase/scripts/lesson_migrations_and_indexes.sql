--liquibase formatted sql

--changeset student:1
create table faculty (
id serial primary key,
name varchar(50),
color varchar(50)
);

create table student (
id serial primary key,
name varchar(50),
age int,
faculty_id int references faculty (id)
);

create table avatar (
id serial primary key,
path varchar(50),
size int,
media_type varchar(50),
student_id int references student (id)
);

--changeset student:2
CREATE INDEX student_name_index ON student (name);

--changeset student:3
CREATE INDEX faculty_name_color_index ON faculty (name, color);