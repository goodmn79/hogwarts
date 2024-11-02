alter table student
alter column age set default 20;

alter table student
add constraint age_constraint check (age>15);

alter table student
alter column name set not null,
add constraint name_unique unique (name);

alter table faculty
add constraint name_color_unique unique (name, color)