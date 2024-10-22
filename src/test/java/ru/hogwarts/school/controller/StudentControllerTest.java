package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.dto.StudentDTO;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    private static final String URL = "/students";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private long studentId;

    private StudentDTO testStudentDTO;

    @BeforeEach
    void setup() {
        studentId = 1L;
        testStudentDTO = new StudentDTO()
                .setId(studentId)
                .setName("test student")
                .setAge(15)
                .setFacultyId(1);
    }

    @Test
    void testAddStudent() throws Exception {
        StudentDTO expectedStudentDTO = testStudentDTO;
        when(studentService.add(expectedStudentDTO)).thenReturn(expectedStudentDTO);

        ResultActions perform = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedStudentDTO)));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedStudentDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedStudentDTO.getName()))
                .andExpect(jsonPath("$.age").value(expectedStudentDTO.getAge()))
                .andExpect(jsonPath("$.facultyId").value(expectedStudentDTO.getFacultyId()))
                .andDo(print());
    }

    @Test
    void testGetCollectionStudents() throws Exception {
        Collection<StudentDTO> mockStudents = Arrays.asList(
                testStudentDTO,
                new StudentDTO()
                        .setId(2L)
                        .setName("second student")
                        .setAge(15)
                        .setFacultyId(1)
        );
        when(studentService.getAll()).thenReturn(mockStudents);

        ResultActions perform = mockMvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON));
        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testStudentDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(testStudentDTO.getName()))
                .andExpect(jsonPath("$[0].age").value(testStudentDTO.getAge()))
                .andExpect(jsonPath("$[0].facultyId").value(testStudentDTO.getFacultyId()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("second student"))
                .andExpect(jsonPath("$[1].age").value(15))
                .andExpect(jsonPath("$[1].facultyId").value(1))
                .andDo(print());
    }

    @Test
    void testGetStudentById() throws Exception {
        StudentDTO expectedStudentDTO = testStudentDTO;
        when(studentService.getById(studentId)).thenReturn(expectedStudentDTO);

        ResultActions perform = mockMvc.perform(get(URL + "/{id}", studentId));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedStudentDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedStudentDTO.getName()))
                .andExpect(jsonPath("$.age").value(expectedStudentDTO.getAge()))
                .andExpect(jsonPath("$.facultyId").value(expectedStudentDTO.getFacultyId()))
                .andDo(print());
    }

    @Test
    void testGetFacultyOfStudent() throws Exception {
        FacultyDTO expectedFacultyDTO = new FacultyDTO()
                .setId(1L)
                .setName("test faculty")
                .setColor("any color");
        when(studentService.getFacultyOfStudent(studentId)).thenReturn(expectedFacultyDTO);

        ResultActions perform = mockMvc.perform(get(URL + "/{id}/faculty", studentId));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedFacultyDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedFacultyDTO.getName()))
                .andExpect(jsonPath("$.color").value(expectedFacultyDTO.getColor()))
                .andDo(print());
    }

    @Test
    void testChangeStudentData() throws Exception {
        StudentDTO expectedStudentDTO = testStudentDTO;
        when(studentService.change(expectedStudentDTO)).thenReturn(expectedStudentDTO);

        ResultActions perform = mockMvc.perform(put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedStudentDTO)));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedStudentDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedStudentDTO.getName()))
                .andExpect(jsonPath("$.age").value(expectedStudentDTO.getAge()))
                .andExpect(jsonPath("$.facultyId").value(expectedStudentDTO.getFacultyId()))
                .andDo(print());
    }

    @Test
    void testDeleteStudent() throws Exception {
        StudentDTO expectedStudentDTO = testStudentDTO;
        when(studentService.deleteById(studentId)).thenReturn(expectedStudentDTO);

        ResultActions perform = mockMvc.perform(delete(URL + "/{id}", studentId));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedStudentDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedStudentDTO.getName()))
                .andExpect(jsonPath("$.age").value(expectedStudentDTO.getAge()))
                .andExpect(jsonPath("$.facultyId").value(expectedStudentDTO.getFacultyId()))
                .andDo(print());
    }
}
