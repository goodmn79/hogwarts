select student."name" , student.age, faculty."name"
from student
left join faculty on student.faculty_id = faculty.id;

select student."name" , student.age
from student
inner join avatar on avatar.student_id = student.id;