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
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {
    private static final String URL = "/faculties";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @MockBean
    private StudentService studentService;

    private long facultyId;

    private FacultyDTO testFacultyDTO;

    @BeforeEach
    void init() {
        facultyId = 1L;
        testFacultyDTO = new FacultyDTO()
                .setId(facultyId)
                .setName("test faculty")
                .setColor("test color");
    }

    @Test
    void testCreateFaculty() throws Exception {
        FacultyDTO expectedFacultyDTO = testFacultyDTO;
        when(facultyService.add(expectedFacultyDTO)).thenReturn(expectedFacultyDTO);

        ResultActions perform = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedFacultyDTO)));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.name").value(expectedFacultyDTO.getName()))
                .andExpect(jsonPath("$.color").value(expectedFacultyDTO.getColor()))
                .andDo(print());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        Collection<FacultyDTO> mockFaculties = Arrays.asList(
                testFacultyDTO,
                new FacultyDTO()
                        .setId(2L)
                        .setName("second faculty")
                        .setColor("second color")
        );
        when(facultyService.getAll()).thenReturn(mockFaculties);

        ResultActions perform = mockMvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON));
        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testFacultyDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(testFacultyDTO.getName()))
                .andExpect(jsonPath("$[0].color").value(testFacultyDTO.getColor()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("second faculty"))
                .andExpect(jsonPath("$[1].color").value("second color"))
                .andDo(print());
    }

    @Test
    void testGetFacultyById() throws Exception {
        FacultyDTO expectedFacultyDTO = testFacultyDTO;
        when(facultyService.getById(anyLong())).thenReturn(expectedFacultyDTO);

        ResultActions perform = mockMvc.perform(get(URL + "/{id}", facultyId));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedFacultyDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedFacultyDTO.getName()))
                .andExpect(jsonPath("$.color").value(expectedFacultyDTO.getColor()))
                .andDo(print());
    }

    @Test
    void testGetStudentsOfFaculty() throws Exception {
        Collection<StudentDTO> mockStudents = Arrays.asList(
                new StudentDTO()
                        .setId(1L)
                        .setName("student one")
                        .setAge(12),
                new StudentDTO()
                        .setId(2L)
                        .setName("student two")
                        .setAge(15)
        );
        when(studentService.findByFacultyId(facultyId)).thenReturn(mockStudents);

        ResultActions perform = mockMvc.perform(get(URL + "/{id}/students", facultyId)
                .accept(MediaType.APPLICATION_JSON));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("student one"))
                .andExpect(jsonPath("$[0].age").value(12))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("student two"))
                .andExpect(jsonPath("$[1].age").value(15))
                .andDo(print());
    }

    @Test
    void testChangeFaculty() throws Exception {
        FacultyDTO expectedFacultyDTO = testFacultyDTO;
        when(facultyService.change(expectedFacultyDTO)).thenReturn(expectedFacultyDTO);

        ResultActions perform = mockMvc.perform(put(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedFacultyDTO)));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedFacultyDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedFacultyDTO.getName()))
                .andExpect(jsonPath("$.color").value(expectedFacultyDTO.getColor()))
                .andDo(print());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        FacultyDTO expectedFacultyDTO = testFacultyDTO;
        when(facultyService.deleteById(anyLong())).thenReturn(expectedFacultyDTO);

        ResultActions perform = mockMvc.perform(delete(URL + "/{id}", facultyId));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedFacultyDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedFacultyDTO.getName()))
                .andExpect(jsonPath("$.color").value(expectedFacultyDTO.getColor()))
                .andDo(print());
    }
}