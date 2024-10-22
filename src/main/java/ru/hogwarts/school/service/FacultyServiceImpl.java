package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.exception.FacultyAlreadyAddedException;
import ru.hogwarts.school.exception.FacultyHasStudentException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.hogwarts.school.mapper.FacultyMapper.mapFromDTO;
import static ru.hogwarts.school.mapper.FacultyMapper.mapToDTO;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    @Override
    public Collection<FacultyDTO> getAllFacultiesByNameOrColor(String searchTerm) {
        Collection<Faculty> filteredByNameFaculty = facultyRepository.findByNameContainsIgnoreCase(searchTerm);
        Collection<Faculty> filteredByColorFaculty = facultyRepository.findByColorContainsIgnoreCase(searchTerm);
        Collection<Faculty> filteredFaculty = Stream.concat(
                        filteredByNameFaculty.stream(),
                        filteredByColorFaculty.stream())
                .collect(Collectors.toSet());
        if (filteredFaculty.isEmpty()) throw new FacultyNotFoundException();
        return mapToDTO(filteredFaculty);
    }

    @Override
    public FacultyDTO add(FacultyDTO facultyDTO) {
        Optional<Faculty> faculty = facultyRepository.findByNameIgnoreCase(facultyDTO.getName());
        if (faculty.isPresent()) throw new FacultyAlreadyAddedException();
        Faculty savedFaculty = facultyRepository.save(mapFromDTO(facultyDTO));
        return mapToDTO(savedFaculty);
    }

    @Override
    public Collection<FacultyDTO> getAll() {
        Collection<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) throw new FacultyNotFoundException();
        return mapToDTO(faculties);
    }

    @Override
    public FacultyDTO getById(long id) {
        Optional<Faculty> foundFaculty = facultyRepository.findById(id);
        return foundFaculty.map(FacultyMapper::mapToDTO).orElseThrow(FacultyNotFoundException::new);
    }

    @Override
    public FacultyDTO change(FacultyDTO facultyDTO) {
        Optional<Faculty> changedFaculty = facultyRepository.findById(facultyDTO.getId());
        if (changedFaculty.isEmpty()) throw new FacultyNotFoundException();
        changedFaculty.map(f -> {
            f.setName(facultyDTO.getName());
            f.setColor(facultyDTO.getColor());
            facultyRepository.save(f);
            return f;
        });
        return changedFaculty.map(FacultyMapper::mapToDTO).orElseThrow(FacultyNotFoundException::new);
    }

    @Override
    public FacultyDTO deleteById(long id) {
        Optional<Faculty> deletedFaculty = facultyRepository.findById(id);
        if (deletedFaculty.isEmpty()) throw new FacultyNotFoundException();
        facultyRepository.delete(deletedFaculty.get());
        return deletedFaculty.map(FacultyMapper::mapToDTO).orElseThrow(FacultyNotFoundException::new);
    }
}
