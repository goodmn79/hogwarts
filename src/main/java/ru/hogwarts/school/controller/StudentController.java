package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentServiceImpl studentService;
    private final AvatarService avatarService;

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.add(studentDTO);
    }

    @GetMapping
    public Collection<StudentDTO> getCollectionStudents(@RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer to,
                                                        @RequestParam(required = false) Integer age) {
        Collection<StudentDTO> students;
        if (from != null && to != null) {
            students = studentService.findByAgeBetween(from, to);
        } else if (age != null) {
            students = studentService.findByAge(age);
        } else {
            students = studentService.getAll();
        }
        return students;
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable long id) {
        return studentService.getById(id);
    }

    @GetMapping("/{id}/faculty")
    public FacultyDTO getFacultyOfStudent(@PathVariable long id) {
        return studentService.getFacultyOfStudent(id);
    }

    @PutMapping
    public StudentDTO changeStudentData(@RequestBody StudentDTO studentDTO) {
        return studentService.change(studentDTO);
    }

    @DeleteMapping("/{id}")
    public StudentDTO deleteStudent(@PathVariable long id) {
        return studentService.deleteById(id);
    }

}
