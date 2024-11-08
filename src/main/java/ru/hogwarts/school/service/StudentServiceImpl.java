package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;

import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.FacultyMapper.mapToDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapToDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository repository;
    private final FacultyService facultyService;

    @Override
    public Collection<StudentDTO> findByFacultyId(long facultyId) {
        LOGGER.info("Invoked method 'findByFacultyId'");
        Collection<Student> students = repository.findAllByFacultyId(facultyId);
        if (students.isEmpty()) {
            LOGGER.error("StudentNotFoundException. Students of faculty with 'id = {}' not found in DB 'Hogwarts'", facultyId);
            throw new StudentNotFoundException();
        }
        LOGGER.debug("The list of faculty students with 'id = {}' size = {}", facultyId, students.size());
        return mapToDTO(students);
    }

    @Override
    public Collection<StudentDTO> findByAgeBetween(int from, int to) {
        LOGGER.info("Invoked method 'findByAgeBetween'");
        Collection<Student> students = repository.findByAgeBetween(from, to);
        if (students.isEmpty()) {
            LOGGER.error("StudentNotFoundException. Students with age between '{}' - '{}', not found in DB 'Hogwarts'", from, to);
            throw new StudentNotFoundException();
        }
        LOGGER.debug("The list of students with age between '{}' - '{}' size = {}", from, to, students.size());
        return mapToDTO(students);
    }

    @Override
    public Collection<StudentDTO> findByAge(int age) {
        LOGGER.info("Invoked method 'findByAge'");
        Collection<Student> students = repository.findByAge(age);
        if (students.isEmpty()) {
            LOGGER.error("StudentNotFoundException. Students with age '{}', not found in DB 'Hogwarts'", age);
            throw new StudentNotFoundException();
        }
        LOGGER.debug("The list of students with age '{}' size = {}", age, students.size());
        return mapToDTO(students);
    }

    @Override
    public FacultyDTO getFacultyOfStudent(long id) {
        LOGGER.info("Invoked method 'getFacultyOfStudent'");
        Optional<Student> foundStudent = repository.findById(id);
        Faculty faculty;
        if (foundStudent.isPresent()) {
            faculty = foundStudent.get().getFaculty();
        } else {
            LOGGER.error("StudentNotFoundException. The student with 'id = {}' not found", id);
            throw new StudentNotFoundException();
        }
        LOGGER.debug("The faculty of student with 'id = {}': {}", id, faculty);
        return mapToDTO(faculty);
    }

    @Override
    public int getCountOfStudents() {
        LOGGER.info("Invoked method 'getCountOfStudents'");
        return repository.getCountOfStudents();
    }

    @Override
    public float getAverageAgeOfStudents() {
        LOGGER.info("Invoked method 'getAverageAgeOfStudents'");
        return repository.getAverageAgeOfStudents();
    }

    @Override
    public Collection<StudentDTO> findLastStudents(int count) {
        LOGGER.info("Invoked method 'findLastStudents'");
        Collection<Student> lastStudents = repository.findLastByIdDesc(count);
        LOGGER.debug("The list of students size = {}", lastStudents.size());
        return mapToDTO(lastStudents);
    }

    @Override
    public StudentDTO add(StudentDTO studentDTO) {
        LOGGER.info("Invoked method 'add'");
        Faculty faculty = mapFromDTO(facultyService.getById(studentDTO.getFacultyId()));
        Student student = repository.save(
                new Student()
                        .setName(studentDTO.getName())
                        .setAge(studentDTO.getAge())
                        .setFaculty(faculty)
        );
        LOGGER.debug("The student {} create and added in DB 'Hogwarts'", student);
        return mapToDTO(student);
    }

    @Override
    public Collection<StudentDTO> getAll() {
        LOGGER.info("Invoked method 'getAll'");
        Collection<Student> students = repository.findAll();
        if (students.isEmpty()) {
            LOGGER.error("StudentNotFoundException. Students not found in DB'Hogwarts'");
            throw new StudentNotFoundException();
        }
        LOGGER.debug("The list of all students size = {}", students.size());
        return mapToDTO(students);
    }

    @Override
    public StudentDTO getById(long id) {
        LOGGER.info("Invoked method 'getById'");
        Optional<Student> foundStudent = repository.findById(id);
        Student student;
        if (foundStudent.isPresent()) {
            student = foundStudent.get();
            LOGGER.debug("The student with 'id = {}': {}", id, student);
        } else {
            LOGGER.error("StudentNotFoundException. Student with 'id = {}' not found in DB'Hogwarts'", id);
            throw new StudentNotFoundException();
        }
        return mapToDTO(student);
    }

    @Override
    public StudentDTO change(StudentDTO studentDTO) {
        LOGGER.warn("Invoked method 'change' changes data about the student");
        Optional<Student> changedStudent = repository.findById(studentDTO.getId());
        if (changedStudent.isPresent()) {
            Student student = changedStudent.get();
            LOGGER.debug("The student before change: {}", student);
            changedStudent.map(s -> {
                s.setName(studentDTO.getName());
                s.setAge(studentDTO.getAge());
                repository.save(s);
                return s;
            });
            LOGGER.debug("The student after change: {}", student);
            return mapToDTO(student);
        } else {
            LOGGER.error("StudentNotFoundException. There is no such student");
            throw new StudentNotFoundException();
        }
    }

    @SneakyThrows
    @Override
    public StudentDTO deleteById(long id) {
        LOGGER.warn("Invoked method 'deleteById' delete data about the student with 'id = {}'", id);
        Optional<Student> student = repository.findById(id);
        if (student.isPresent()) {
            Student deletedStudent = student.get();
            repository.delete(deletedStudent);
            LOGGER.debug("Student with 'id = {}' successfully deleted", id);
            return mapToDTO(deletedStudent);
        } else {
            LOGGER.error("StudentNotFoundException. There is no such student with 'id = {}'", id);
            throw new StudentNotFoundException();
        }
    }
}
