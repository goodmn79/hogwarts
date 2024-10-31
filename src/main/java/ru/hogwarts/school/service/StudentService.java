package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;

import java.util.Collection;

public interface StudentService extends SchoolService<StudentDTO> {
    Collection<StudentDTO> findByFacultyId(long facultyId);

    Collection<StudentDTO> findByAgeBetween(int from, int to);

    Collection<StudentDTO> findByAge(int age);

    FacultyDTO getFacultyOfStudent(long id);

    int getCountOfStudents();

    float getAverageAgeOfStudents();

    Collection<StudentDTO> findLastStudents(int count);
}
