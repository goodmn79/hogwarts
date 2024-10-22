package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;

import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.FacultyMapper.mapToDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapToDTO;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository repository;
    private final FacultyService facultyService;

    @Override
    public Collection<StudentDTO> findByFacultyId(long facultyId) {
        Collection<Student> students = repository.findAllByFacultyId(facultyId);
        if (students.isEmpty()) throw new StudentNotFoundException();
        return mapToDTO(students);
    }

    @Override
    public Collection<StudentDTO> findByAgeBetween(int from, int to) {
        Collection<Student> students = repository.findByAgeBetween(from, to);
        if (students.isEmpty()) throw new StudentNotFoundException();
        return StudentMapper.mapToDTO(students);
    }

    @Override
    public Collection<StudentDTO> findByAge(int age) {
        Collection<Student> students = repository.findByAge(age);
        if (students.isEmpty()) throw new StudentNotFoundException();
        return StudentMapper.mapToDTO(students);
    }

    @Override
    public FacultyDTO getFacultyOfStudent(long id) {
        Student foundStudent = repository.findById(id).orElseThrow(StudentNotFoundException::new);
        Faculty faculty = foundStudent.getFaculty();
        return mapToDTO(faculty);
    }

    @Override
    public StudentDTO add(StudentDTO studentDTO) {
        Faculty faculty = mapFromDTO(facultyService.getById(studentDTO.getFacultyId()));
        Student addedStudent = new Student()
                .setName(studentDTO.getName())
                .setAge(studentDTO.getAge())
                .setFaculty(faculty);
        return StudentMapper.mapToDTO(repository.save(addedStudent));
    }

    @Override
    public Collection<StudentDTO> getAll() {
        Collection<Student> students = repository.findAll();
        if (students.isEmpty()) throw new StudentNotFoundException();
        return StudentMapper.mapToDTO(students);
    }

    @Override
    public StudentDTO getById(long id) {
        Optional<Student> foundStudent = repository.findById(id);
        return foundStudent.map(StudentMapper::mapToDTO).orElseThrow(StudentNotFoundException::new);
    }

    @Override
    public StudentDTO change(StudentDTO studentDTO) {
        Optional<Student> foundStudent = repository.findById(studentDTO.getId());
        foundStudent.map(s -> {
            s.setName(studentDTO.getName());
            s.setAge(studentDTO.getAge());
            repository.save(s);
            return s;
        });
        return StudentMapper.mapToDTO(foundStudent.orElseThrow(StudentNotFoundException::new));
    }

    @Override
    public StudentDTO deleteById(long id) {
        Optional<Student> foundStudent = repository.findById(id);
        foundStudent.ifPresent(repository::delete);
        return StudentMapper.mapToDTO(foundStudent.orElseThrow(StudentNotFoundException::new));
    }
}
