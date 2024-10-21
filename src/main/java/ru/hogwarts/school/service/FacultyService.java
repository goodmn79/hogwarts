package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;

import java.util.Collection;

public interface FacultyService extends SchoolService<FacultyDTO> {
    Collection<FacultyDTO> getAllFacultiesByNameOrColor(String searchTerm);

    Collection<StudentDTO> getStudentsOfFaculty(long id);
}
