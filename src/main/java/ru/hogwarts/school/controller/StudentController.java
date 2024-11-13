package ru.hogwarts.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.add(studentDTO);
    }

    @GetMapping
    public ResponseEntity<Collection<StudentDTO>> getCollectionStudents(@RequestParam(required = false) Integer from,
                                                                        @RequestParam(required = false) Integer to,
                                                                        @RequestParam(required = false) Integer age,
                                                                        @RequestParam(required = false) Integer count) {
        Collection<StudentDTO> students;
        if (nullable(from, to, age, count)) {
            students = studentService.getAll();
        } else if (nullable(age, count) && !nullable(from, to)) {
            students = studentService.findByAgeBetween(from, to);
        } else if (nullable(count, from, to)) {
            students = studentService.findByAge(age);
        } else if (nullable(from, to, age)) {
            int countOfStudents = studentService.getCountOfStudents();
            if (count > countOfStudents) count = countOfStudents;
            students = studentService.findLastStudents(count);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(students);
    }

    @GetMapping("list_of_names_starting_with_letter")
    public ResponseEntity<Collection<String>> studentList(@RequestParam char letter) {
        if (!Character.isAlphabetic(letter)) return ResponseEntity.badRequest().build();
        Collection<String> studentsNames = studentService.getNamesStartingWith(letter);
        return ResponseEntity.ok(studentsNames);
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

    @PutMapping
    public StudentDTO changeStudentData(@RequestBody StudentDTO studentDTO) {
        return studentService.change(studentDTO);
    }

    @DeleteMapping("/{id}")
    public StudentDTO deleteStudent(@PathVariable long id) {
        try {
            AvatarDTO avatarDTO = avatarService.getAvatar(id);
            avatarService.deleteAvatar(avatarDTO.getId());
            return studentService.deleteById(id);
        } catch (Exception e) {
            return studentService.deleteById(id);
        }
    }

    private boolean nullable(Integer... any) {
        for (Integer i : any) {
            if (i != null) return false;
        }
        return true;
    }
}
