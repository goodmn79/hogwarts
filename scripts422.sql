create table car (
car_id serial primary key,
car_make varchar(50) not null,
car_model varchar(100) not null,
car_price int not null check(car_price > 0)
);

create table person (
person_id serial primary key,
person_name varchar(20) not null,
person_birthdate date not null,
driver_license boolean default false,
car_id int references car(car_id),
constraint check_valid_birthdate check(age(person_birthdate) >= interval'18 years'),
constraint unique_person unique(person_id, person_name, person_birthdate),
constraint check_driver_license check((driver_license = false and car_id is null) or (driver_license = true))
);