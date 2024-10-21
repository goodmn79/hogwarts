package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    public FacultyDTO createFaculty(@RequestBody FacultyDTO facultyDTO) {
        return service.add(facultyDTO);
    }

    @GetMapping
    public Collection<FacultyDTO> getAllFaculties(@RequestParam(required = false) String search_term) {
        Collection<FacultyDTO> faculties;
        if (search_term == null) {
            faculties = service.getAll();
        } else {
            faculties = service.getAllFacultiesByNameOrColor(search_term);
        }
        return faculties;
    }

    @GetMapping("/{id}")
    public FacultyDTO getFacultyById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/students")
    public Collection<StudentDTO> getStudentsOfFaculty(@PathVariable long id) {
        return service.getStudentsOfFaculty(id);

    }

    @PutMapping
    public FacultyDTO changeFaculty(@RequestBody FacultyDTO facultyDTO) {
        return service.change(facultyDTO);
    }

    @DeleteMapping("/{id}")
    public FacultyDTO deleteFaculty(@PathVariable long id) {
        return service.deleteById(id);
    }
}
