package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.add(studentDTO);
    }

    @GetMapping
    public Collection<StudentDTO> getStudents(@RequestParam(required = false) Integer from,
                                              @RequestParam(required = false) Integer to,
                                              @RequestParam(required = false) Integer age,
                                              @RequestParam(required = false) Integer count) {
        return studentService.getStudents(from, to, age, count);
    }

    @GetMapping("list_of_names_starting_with_letter")
    public Collection<String> studentList(@RequestParam char latter) {
        return studentService.getNamesStartingWith(latter);
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable long id) {
        return studentService.getById(id);
    }

    @GetMapping("/count")
    public int getCountOfStudents() {
        return studentService.getCountOfStudents();
    }

    @GetMapping("/average_age_of_students")
    public double getAverageAgeOfStudents() {
        return studentService.getAverageAgeOfStudents();
    }

    @GetMapping("/{id}/faculty")
    public FacultyDTO getFacultyOfStudent(@PathVariable long id) {
        return studentService.getFacultyOfStudent(id);
    }

    @GetMapping("/print-parallel")
    public void printParallel() {
        studentService.printParallel();
    }

    @GetMapping("/students/print-synchronized")
    public void printSynchronized() {
        studentService.printSynchronized();
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
