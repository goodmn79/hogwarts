package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchoolApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    FacultyController facultyController;
    @Autowired
    StudentController studentController;

    @Test
    void contextLoads() {
        assertThat(facultyController).isNotNull();
        assertThat(studentController).isNotNull();
    }

}
