package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.exception.FacultyAlreadyAddedException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.InvalidDeletionRequestException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.mapper.FacultyMapper.mapToDTO;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {
    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyServiceImpl facultyService;

    private FacultyDTO testFacultyDTO;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        String name = "test name";
        String color = "test color";

        testFaculty = new Faculty()
                .setId(1L)
                .setName(name)
                .setColor(color)
                .setStudents(Collections.EMPTY_LIST);

        testFacultyDTO = mapToDTO(testFaculty);
    }

    @Test
    void getFaculties() {
        Collection<Faculty> filteredByName =
                Stream.of(mock(Faculty.class), mock(Faculty.class)).toList();
        Collection<Faculty> filteredByColor =
                Stream.of(mock(Faculty.class), mock(Faculty.class)).toList();
        Collection<FacultyDTO> expected = Stream.concat(
                        mapToDTO(filteredByName).stream(),
                        mapToDTO(filteredByColor).stream())
                .toList();
        when(facultyRepository.findByNameContainsIgnoreCase(anyString())).thenReturn(filteredByName);
        when(facultyRepository.findByColorContainsIgnoreCase(anyString())).thenReturn(filteredByColor);

        Collection<FacultyDTO> actual = facultyService.getFaculties(anyString());

        assertEquals(actual.size(), expected.size());
    }

    @Test
    void testGetFaculties_whenNoMatches_FacultyNotFoundException() {
        String searchTerm = "NonExistent";
        when(facultyRepository.findByNameContainsIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());
        when(facultyRepository.findByColorContainsIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

        assertThrows(FacultyNotFoundException.class, () -> facultyService.getFaculties(searchTerm));
    }

    @Test
    void add_ShouldSaveNewFaculty() {
        when(facultyRepository.findByNameIgnoreCase(testFacultyDTO.getName()))
                .thenReturn(Optional.empty());
        when(facultyRepository.save(any(Faculty.class)))
                .thenReturn(testFaculty);

        FacultyDTO actualFacultyDTO = facultyService.add(testFacultyDTO);

        verify(facultyRepository, times(1)).save(any(Faculty.class));
        assertEquals(testFacultyDTO.getName(), actualFacultyDTO.getName());
        assertEquals(testFacultyDTO.getColor(), actualFacultyDTO.getColor());
    }

    @Test
    void add_whenFacultyAlreadyExists_ShouldThrowException() {
        when(facultyRepository.findByNameIgnoreCase(testFacultyDTO.getName()))
                .thenReturn(Optional.of(testFaculty));

        assertThrows(FacultyAlreadyAddedException.class, () -> facultyService.add(testFacultyDTO));
        verify(facultyRepository, never()).save(any(Faculty.class));
    }

    @Test
    void getAll_shouldReturnCollection() {
        List<Faculty> expected = Stream.of(testFaculty, mock(Faculty.class), mock(Faculty.class))
                .collect(Collectors.toCollection(ArrayList::new));
        when(facultyRepository.findAll()).thenReturn(expected);

        Collection<FacultyDTO> actual = facultyService.getAll();

        verify(facultyRepository, times(1)).findAll();
        assertEquals(expected.size(), actual.size());
        assertIterableEquals(mapToDTO(expected), actual);
    }

    @Test
    void getAll_whenFacultyNotFound_shouldThrowException() {
        when(facultyRepository.findAll())
                .thenReturn(new ArrayList<>());

        assertThrows(FacultyNotFoundException.class, () -> facultyService.getAll());
    }

    @Test
    void getById_shouldReturnFacultyWithThisId() {
        when(facultyRepository.findById(anyLong()))
                .thenReturn(Optional.of(testFaculty));

        FacultyDTO actualFacultyDTO = facultyService.getById(anyLong());

        verify(facultyRepository, times(1)).findById(anyLong());
        assertEquals(testFacultyDTO.getName(), actualFacultyDTO.getName());
        assertEquals(testFacultyDTO.getColor(), actualFacultyDTO.getColor());
    }

    @Test
    void getById_whenFacultyNotFound_ShouldThrowException() {
        when(facultyRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(FacultyNotFoundException.class, () -> facultyService.getById(anyLong()));
        verify(facultyRepository, times(1)).findById(anyLong());
    }

    @Test
    void change_shouldReturnChangedFaculty() {
        when(facultyRepository.findById(testFacultyDTO.getId())).thenReturn(Optional.of(testFaculty));
        Faculty expected = new Faculty()
                .setId(testFaculty.getId())
                .setName("new name")
                .setColor("new color");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(expected);

        FacultyDTO actual = facultyService.change(mapToDTO(expected));

        verify(facultyRepository, times(1)).save(any(Faculty.class));
        verify(facultyRepository, times(1)).findById(anyLong());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getColor(), actual.getColor());
        assertEquals(testFaculty.getName(), actual.getName());
        assertEquals(testFaculty.getColor(), actual.getColor());
    }

    @Test
    void change_whenFacultyNotFound_ShouldThrowException() {
        when(facultyRepository.findById(testFacultyDTO.getId()))
                .thenReturn(Optional.empty());

        assertThrows(FacultyNotFoundException.class, () -> facultyService.change(testFacultyDTO));
        verify(facultyRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteById_shouldReturnDeletedFaculty() {
        when(facultyRepository.findById(testFacultyDTO.getId()))
                .thenReturn(Optional.of(testFaculty));

        FacultyDTO actualFacultyDTO = facultyService.deleteById(testFacultyDTO.getId());

        verify(facultyRepository, times(1)).delete(testFaculty);
        assertEquals(testFacultyDTO, actualFacultyDTO);
    }

    @Test
    void deleteById_whenFacultyNotFound_ShouldThrowException() {
        when(facultyRepository.findById(testFacultyDTO.getId()))
                .thenReturn(Optional.empty());

        assertThrows(FacultyNotFoundException.class, () -> facultyService.deleteById(testFacultyDTO.getId()));
        verify(facultyRepository, times(0)).delete(any(Faculty.class));
    }

    @Test
    void testDeleteById_InvalidDeletionRequestException() {
        testFaculty.setStudents(Collections.singletonList(new Student()));  // Добавляем студента

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(testFaculty));

        assertThrows(InvalidDeletionRequestException.class, () -> facultyService.deleteById(1L));
        verify(facultyRepository, never()).delete(any());
    }
}