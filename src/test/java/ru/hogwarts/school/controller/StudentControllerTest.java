package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.FacultyMapper.mapToDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.StudentMapper.mapToDTO;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    private final Random random = new Random();
    @LocalServerPort
    private int port;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    private FacultyDTO testFacultyDTO;

    private StudentDTO testStudentDTO;


    @BeforeEach
    void init() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        Faculty faculty = facultyRepository.save(
                new Faculty()
                        .setName("test faculty")
                        .setColor("test color"));
        testFacultyDTO = mapToDTO(faculty);

        Student student = studentRepository.save(
                new Student()
                        .setName("test student")
                        .setAge(random.nextInt(12, 14))
                        .setFaculty(faculty));
        testStudentDTO = mapToDTO(student);
    }

    @Test
    void testAddStudent() {
        StudentDTO expectedStudentDTO =
                new StudentDTO()
                        .setName("any student")
                        .setAge(random.nextInt())
                        .setFacultyId(testFacultyDTO.getId());

        ResponseEntity<StudentDTO> response =
                restTemplate.postForEntity(url(port), expectedStudentDTO, StudentDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDTO actualStudentDTO = response.getBody();
        assertThat(actualStudentDTO).isNotNull();
        assertThat(actualStudentDTO.getName()).isEqualTo(expectedStudentDTO.getName());
        assertThat(actualStudentDTO.getAge()).isEqualTo(expectedStudentDTO.getAge());
        assertThat(actualStudentDTO.getFacultyId()).isEqualTo(expectedStudentDTO.getFacultyId());
    }

    @Test
    void testGetCollectionStudents() {
        ResponseEntity<Collection> response =
                restTemplate.getForEntity(
                        url(port), Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void testGetStudentsByAgeBetween() {
        studentRepository.save(new Student()
                .setName("any student")
                .setAge(13)
                .setFaculty(mapFromDTO(testFacultyDTO)));
        ResponseEntity<Collection> response = restTemplate.getForEntity(
                url(port) + "?from=12&to=14",
                Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    void testGetStudentsByAge() {
        studentRepository.save(new Student()
                .setName("any student")
                .setAge(15)
                .setFaculty(mapFromDTO(testFacultyDTO)));

        ResponseEntity<Collection> response =
                restTemplate.getForEntity(
                        url(port) + "?age=15",
                        Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void testGetLastStudents() {
        Faculty faculty = mapFromDTO(testFacultyDTO);
        StudentDTO student1 = new StudentDTO()
                .setName("Student 1")
                .setAge(10)
                .setFacultyId(faculty.getId());
        StudentDTO student2 = new StudentDTO()
                .setName("Student 2")
                .setAge(10)
                .setFacultyId(faculty.getId());
        StudentDTO student3 = new StudentDTO()
                .setName("Student 3")
                .setAge(10)
                .setFacultyId(faculty.getId());
        studentRepository.save(mapFromDTO(student1));
        studentRepository.save(mapFromDTO(student2));
        studentRepository.save(mapFromDTO(student3));

        ResponseEntity<Collection> response =
                restTemplate.getForEntity(url(port) + "?count=2", Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);

        List<String> names = (List<String>) response.getBody().stream()
                .map(student -> ((Map<String, Object>) student).get("name").toString())
                .collect(Collectors.toList());

        assertThat(names)
                .contains(student3.getName())
                .contains(student2.getName())
                .doesNotContain(student1.getName());
    }

    @Test
    void testGetStudentById() {
        ResponseEntity<StudentDTO> response = restTemplate.getForEntity(
                url(port, testStudentDTO.getId()), StudentDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDTO actualStudentDTO = response.getBody();
        assertThat(actualStudentDTO).isNotNull();
        assertThat(actualStudentDTO.getName()).isEqualTo(testStudentDTO.getName());
        assertThat(actualStudentDTO.getAge()).isEqualTo(testStudentDTO.getAge());
    }

    @Test
    void testGetFacultyOfStudent() {
        ResponseEntity<FacultyDTO> response =
                restTemplate.getForEntity(url(port, testStudentDTO.getId()) + "/faculty", FacultyDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FacultyDTO actualFacultyDTO = response.getBody();
        assertThat(actualFacultyDTO).isNotNull();
        assertThat(actualFacultyDTO.getId()).isEqualTo(testStudentDTO.getFacultyId());
    }

    @Test
    void testChangeStudentData() {
        long id = testStudentDTO.getId();
        StudentDTO expectedStudentDTO = new StudentDTO()
                .setId(id)
                .setName("new student")
                .setAge(random.nextInt(50, 100));

        restTemplate.put(url(port), expectedStudentDTO);

        ResponseEntity<StudentDTO> response = restTemplate.getForEntity(url(port, id), StudentDTO.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentDTO actualStudentDTO = response.getBody();
        assertThat(actualStudentDTO).isNotNull();
        assertThat(actualStudentDTO.getName()).isEqualTo(expectedStudentDTO.getName());
        assertThat(actualStudentDTO.getAge()).isEqualTo(expectedStudentDTO.getAge());
    }

    @Test
    void testDeleteStudent() {
        long id = testStudentDTO.getId();

        restTemplate.delete(url(port, id), StudentDTO.class);

        ResponseEntity<StudentDTO> response = restTemplate.getForEntity(url(port, id), StudentDTO.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String url(int port) {
        String url = "http://localhost:";
        return url + port + "/students";
    }

    private String url(int port, long id) {
        return url(port) + "/" + id;
    }
}
