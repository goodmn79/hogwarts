package ru.hogwarts.school.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapToDTO;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyService facultyService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private FacultyDTO testFacultyDTO;
    private StudentDTO testStudentDTO;
    private Student testStudent;
    private List<Student> testStudents;

    @BeforeEach
    void setUp() {
        String name = "test name";
        String color = "test color";

        testFacultyDTO = new FacultyDTO()
                .setId(1L)
                .setName(name)
                .setColor(color);
        testStudentDTO = new StudentDTO()
                .setId(1L)
                .setName(name)
                .setAge(12)
                .setFacultyId(testFacultyDTO.getId());

        Faculty testFaculty = mapFromDTO(testFacultyDTO);
        testStudent = mapFromDTO(testStudentDTO)
                .setFaculty(testFaculty);

        testStudents = Arrays.asList(
                new Student().setName("Student1"),
                new Student().setName("Student2"),
                new Student().setName("Student3"),
                new Student().setName("Student4"),
                new Student().setName("Student5"),
                new Student().setName("Student6"));
    }

    @Test
    void findByFacultyId_shouldReturnAllListForPrintOfFaculty() {
        Collection<Student> students =
                Stream.of(mock(Student.class), mock(Student.class), mock(Student.class)).toList();
        when(studentRepository.findAllByFacultyId(anyLong())).thenReturn(students);

        Collection<StudentDTO> actual = studentService.findByFacultyId(anyLong());

        verify(studentRepository, times(1)).findAllByFacultyId(anyLong());
        assertIterableEquals(mapToDTO(students), actual);
    }

    @Test
    void findByFacultyId_whenListForPrintNotFound_shouldThrowException() {
        when(studentRepository.findAllByFacultyId(anyLong())).thenReturn(new ArrayList<>());

        assertThrows(StudentNotFoundException.class, () -> studentService.findByFacultyId(anyLong()));
    }

    @Test
    void findByAgeBetween_shouldReturnAllListForPrintWithThisAge() {
        Collection<Student> students =
                Stream.of(mock(Student.class), mock(Student.class), mock(Student.class)).toList();
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(students);

        Collection<StudentDTO> actual = studentService.findByAgeBetween(anyInt(), anyInt());

        verify(studentRepository, times(1)).findByAgeBetween(anyInt(), anyInt());
        assertIterableEquals(mapToDTO(students), actual);
    }

    @Test
    void findByAgeBetween_whenListForPrintNotFound_shouldThrowException() {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(new ArrayList<>());

        assertThrows(StudentNotFoundException.class, () -> studentService.findByAgeBetween(anyInt(), anyInt()));
    }

    @Test
    void findByAge_shouldReturnAllListForPrintWithThisAge() {
        Collection<Student> students =
                Stream.of(mock(Student.class), mock(Student.class), mock(Student.class)).toList();
        when(studentRepository.findByAge(anyInt())).thenReturn(students);

        Collection<StudentDTO> actual = studentService.findByAge(anyInt());

        verify(studentRepository, times(1)).findByAge(anyInt());
        assertIterableEquals(mapToDTO(students), actual);
    }

    @Test
    void findByAge_whenListForPrintNotFound_shouldThrowException() {
        when(studentRepository.findByAge(anyInt())).thenReturn(new ArrayList<>());

        assertThrows(StudentNotFoundException.class, () -> studentService.findByAge(anyInt()));
    }

    @Test
    void getFacultyOfStudent_shouldReturnFacultyOfThisStudent() {
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        FacultyDTO actual = studentService.getFacultyOfStudent(testStudent.getId());

        verify(studentRepository, times(1)).findById(testStudent.getId());
        assertEquals(testFacultyDTO, actual);
    }

    @Test
    void getFacultyOfStudent_whenStudentNotFound_shouldThrowException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getFacultyOfStudent(anyLong()));
    }

    @Test
    void testPrintParallel() {
        when(studentRepository.findAll()).thenReturn(testStudents);

        String output = captureSystemOutput(() -> studentService.printParallel());
        System.out.println("Ожидаемый результат:\n" + output + " конец!");

        assertTrue(output.contains("Student1"));
        assertTrue(output.contains("Student2"));
        assertTrue(output.contains("Student3"));
        assertTrue(output.contains("Student4"));
        assertTrue(output.contains("Student5"));
        assertTrue(output.contains("Student6"));
    }

    @Test
    public void testPrintSynchronized() {
        when(studentRepository.findAll()).thenReturn(testStudents);

        String output = captureSystemOutput(() -> studentService.printSynchronized());
        System.out.println("Ожидаемый результат:\n" + output + " конец!");

        assertTrue(output.contains("Student1"));
        assertTrue(output.contains("Student2"));
        assertTrue(output.contains("Student3"));
        assertTrue(output.contains("Student4"));
        assertTrue(output.contains("Student5"));
        assertTrue(output.contains("Student6"));
    }

    @Test
    void add_shouldReturnNewStudent() {
        when(facultyService.getById(1L)).thenReturn(testFacultyDTO);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        StudentDTO actual = studentService.add(testStudentDTO);

        verify(facultyService).getById(1L);
        verify(studentRepository).save(any(Student.class));
        assertEquals(testStudent.getName(), actual.getName());
        assertEquals(testStudent.getAge(), actual.getAge());
        assertEquals(testStudent.getId(), actual.getFacultyId());
    }

    @Test
    void getAll_shouldReturnAllListForPrint() {
        List<Student> students = Stream.of(mock(Student.class), mock(Student.class), mock(Student.class)).toList();
        when(studentRepository.findAll()).thenReturn(students);

        Collection<StudentDTO> actual = studentService.getAll();

        verify(studentRepository, times(1)).findAll();
        assertIterableEquals(mapToDTO(students), actual);
    }

    @Test
    void getAll_whenListForPrintNotFound_shouldThrowException() {
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(StudentNotFoundException.class, () -> studentService.getAll());
    }

    @Test
    void getById_shouldReturnFoundStudent() {
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        StudentDTO actual = studentService.getById(testStudent.getId());

        verify(studentRepository, times(1)).findById(anyLong());
        assertEquals(mapToDTO(testStudent), actual);
    }

    @Test
    void getById_whenStudentNotFound_shouldThrowException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getById(anyLong()));
    }

    @Test
    void change_shouldReturnChangedStudent() {
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));
        Student expected = new Student()
                .setId(testStudent.getId())
                .setName("new name")
                .setAge(15);
        when(studentRepository.save(any(Student.class))).thenReturn(expected);

        StudentDTO actual = studentService.change(mapToDTO(expected));

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(studentRepository, times(1)).findById(anyLong());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(testStudent.getName(), actual.getName());
        assertEquals(testStudent.getAge(), actual.getAge());
    }

    @Test
    void change_whenStudentNotFound_shouldThrowException() {
        when(studentRepository.findById(testStudentDTO.getId())).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.change(testStudentDTO));
    }

    @Test
    void deleteById_shouldReturnDeletedStudent() {
        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        StudentDTO actual = studentService.deleteById(testStudent.getId());

        verify(studentRepository, times(1)).delete(any(Student.class));
        assertEquals(mapToDTO(testStudent), actual);
    }

    @Test
    void deleteById_whenStudentNotFound_shouldThrowException() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.deleteById(anyLong()));
    }

    @SneakyThrows
    private String captureSystemOutput(Runnable task) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        CountDownLatch latch = new CountDownLatch(1);

        try {
            new Thread(() -> {
                task.run();
                latch.countDown();
            }).start();

            latch.await();
        } finally {
            System.setOut(originalOut);
        }
        return out.toString();
    }
}