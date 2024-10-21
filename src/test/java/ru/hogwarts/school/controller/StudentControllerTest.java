package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private final Random random = new Random();

    private final String url = "http://localhost:";

    private FacultyDTO testFacultyDTO;

    private StudentDTO testStudentDTO;

    @BeforeEach
    void setup() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();


        testFacultyDTO = restTemplate.postForObject(
                urlForFaculties(port),
                new FacultyDTO()
                        .setName("test faculty")
                        .setColor("test color"),
                FacultyDTO.class);

        StudentDTO studentDTO = new StudentDTO()
                .setName("test name")
                .setAge(random.nextInt(12, 14))
                .setFacultyId(testFacultyDTO.getId());
        testStudentDTO = restTemplate.postForObject(url(port), studentDTO, StudentDTO.class);
    }

    @Test
    void testAddStudent() {
        StudentDTO expectedStudentDTO = new StudentDTO()
                .setName("any name")
                .setAge(random.nextInt())
                .setFacultyId(testFacultyDTO.getId());

        ResponseEntity<StudentDTO> response = restTemplate.postForEntity(
                url(port), expectedStudentDTO, StudentDTO.class);

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
                .setName("any name")
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
                .setName("any name")
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
                .setName("new name")
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
        return url + port + "/students";
    }

    private String url(int port, long id) {
        return url(port) + "/" + id;
    }

    private String urlForFaculties(int port) {
        return url + port + "/faculties";
    }
}
