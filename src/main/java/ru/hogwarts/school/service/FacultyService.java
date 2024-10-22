package ru.hogwarts.school.service;

import ru.hogwarts.school.dto.FacultyDTO;

import java.util.Collection;

public interface FacultyService extends SchoolService<FacultyDTO> {
    Collection<FacultyDTO> getAllFacultiesByNameOrColor(String searchTerm);
}
