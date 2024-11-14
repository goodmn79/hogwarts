package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyService facultyService;
    private final StudentService studentService;

    @PostMapping
    public FacultyDTO createFaculty(@RequestBody FacultyDTO facultyDTO) {
        return facultyService.add(facultyDTO);
    }

    @GetMapping
    public Collection<FacultyDTO> getFaculties(@RequestParam(required = false) String search_term) {
        return facultyService.getFaculties(search_term);
    }

    @GetMapping("/{id}")
    public FacultyDTO getFacultyById(@PathVariable long id) {
        return facultyService.getById(id);
    }

    @GetMapping("/{id}/students")
    public Collection<StudentDTO> getStudentsOfFaculty(@PathVariable long id) {
        return studentService.findByFacultyId(id);
    }

    @GetMapping("/longest_faculty_name")
    public String longestFacultyName() {
        return facultyService.longestFacultyName();
    }

    @PutMapping
    public FacultyDTO changeFaculty(@RequestBody FacultyDTO facultyDTO) {
        return facultyService.change(facultyDTO);
    }

    @DeleteMapping("/{id}")
    public FacultyDTO deleteFaculty(@PathVariable long id) {
        return facultyService.deleteById(id);
    }
}
