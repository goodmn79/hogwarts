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
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.hogwarts.school.mapper.StudentMapper.mapFromDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private FacultyDTO testFacultyDTO;

    @BeforeEach
    void init() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        testFacultyDTO = restTemplate.postForObject(
                url(port),
                new FacultyDTO()
                        .setName("test faculty")
                        .setColor("test color"),
                FacultyDTO.class);
    }

    @Test
    void testCreateFaculty() {
        String name = "actual name";
        String color = "actual color";
        ResponseEntity<FacultyDTO> response =
                restTemplate.postForEntity(
                        url(port),
                        new FacultyDTO()
                                .setName(name)
                                .setColor(color),
                        FacultyDTO.class);
        FacultyDTO actualFacultyDTO = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualFacultyDTO).isNotNull();
        assertThat(actualFacultyDTO.getName()).isEqualTo(name);
        assertThat(actualFacultyDTO.getColor()).isEqualTo(color);
    }

    @Test
    void testGetAllFaculties() {
        ResponseEntity<Collection> response =
                restTemplate.getForEntity(url(port), Collection.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void testGetFacultyById() {
        long facultyId = testFacultyDTO.getId();

        ResponseEntity<FacultyDTO> response = restTemplate.getForEntity(url(port, facultyId), FacultyDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FacultyDTO actualFacultyDTO = response.getBody();
        assertThat(actualFacultyDTO.getId()).isEqualTo(facultyId);
        assertThat(actualFacultyDTO.getName()).isEqualTo(testFacultyDTO.getName());
        assertThat(actualFacultyDTO.getColor()).isEqualTo(testFacultyDTO.getColor());
    }

    @Test
    void testGetStudentsOfFaculty() {
        long facultyId = testFacultyDTO.getId();
        StudentDTO expectedStudentDTO =
                new StudentDTO()
                        .setName("any name")
                        .setAge(12)
                        .setFacultyId(facultyId);
        studentRepository.save(mapFromDTO(expectedStudentDTO));

        ResponseEntity<Collection> response = restTemplate.getForEntity(url(port, facultyId) + "/students", Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    void testChangeFaculty() {
        long facultyId = testFacultyDTO.getId();
        FacultyDTO expectedFacultyDTO =
                new FacultyDTO()
                        .setId(facultyId)
                        .setName("new name")
                        .setColor("another color");

        restTemplate.put(url(port), expectedFacultyDTO);

        ResponseEntity<FacultyDTO> response = restTemplate.getForEntity(url(port, facultyId), FacultyDTO.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FacultyDTO actualFacultyDTO = response.getBody();
        assertThat(actualFacultyDTO).isNotNull()
                .isEqualTo(expectedFacultyDTO);
    }

    @Test
    void testDeleteFaculty() {
        long facultyId = testFacultyDTO.getId();

        restTemplate.delete(url(port, facultyId));

        ResponseEntity<FacultyDTO> response = restTemplate.getForEntity(url(port, facultyId), FacultyDTO.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String url(int port) {
        return "http://localhost:" + port + "/faculties";
    }

    private String url(int port, long id) {
        return url(port) + "/" + id;
    }
}