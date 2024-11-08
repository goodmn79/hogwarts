package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDTO;
import ru.hogwarts.school.exception.FacultyAlreadyAddedException;
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
@Transactional
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacultyServiceImpl.class);
    private final FacultyRepository facultyRepository;


    @Override
    public Collection<FacultyDTO> getAllFacultiesByNameOrColor(String searchTerm) {
        LOGGER.info("Invoked method 'getAllFacultiesByNameOrColor' to get list of all faculties with name or color {}", searchTerm);
        Collection<Faculty> filteredByNameFaculty = facultyRepository.findByNameContainsIgnoreCase(searchTerm);
        Collection<Faculty> filteredByColorFaculty = facultyRepository.findByColorContainsIgnoreCase(searchTerm);
        Collection<Faculty> filteredFaculty = Stream.concat(
                        filteredByNameFaculty.stream(),
                        filteredByColorFaculty.stream())
                .collect(Collectors.toSet());
        if (filteredFaculty.isEmpty()) {
            LOGGER.error("FacultyNotFoundException. The faculty {} not found", searchTerm);
            throw new FacultyNotFoundException();
        }
        LOGGER.debug("The faculties list size: {}", filteredFaculty.size());
        return mapToDTO(filteredFaculty);
    }

    @Override
    public FacultyDTO add(FacultyDTO facultyDTO) {
        LOGGER.info("Invoked method 'add', for create and added faculty to DB 'Hogwarts'");
        Optional<Faculty> faculty = facultyRepository.findByNameIgnoreCase(facultyDTO.getName());
        if (faculty.isPresent()) {
            LOGGER.error("FacultyAlreadyAddedException. Faculty {} already exist on DB 'Hogwarts'", facultyDTO.getName());
            throw new FacultyAlreadyAddedException();
        }
        Faculty savedFaculty = facultyRepository.save(mapFromDTO(facultyDTO));
        LOGGER.debug("Faculty {} create and saved to DB 'Hogwarts'", facultyDTO);
        return mapToDTO(savedFaculty);
    }

    @Override
    public Collection<FacultyDTO> getAll() {
        LOGGER.info("Invoked method 'getAll' to get list of all faculties");
        Collection<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            LOGGER.error("FacultyNotFoundException. The faculties not exist");
            throw new FacultyNotFoundException();
        }
        LOGGER.debug("The count of all faculties: {}", faculties.size());
        return mapToDTO(faculties);
    }

    @Override
    public FacultyDTO getById(long id) {
        LOGGER.info("Invoked method 'getById'");
        Optional<Faculty> foundFaculty = facultyRepository.findById(id);
        if (foundFaculty.isEmpty()) {
            LOGGER.error("FacultyNotFoundException. The faculty with 'id = {}' not found", id);
        } else {
            LOGGER.debug(foundFaculty.get().toString());
        }
        return foundFaculty.map(FacultyMapper::mapToDTO).orElseThrow(FacultyNotFoundException::new);
    }

    @Override
    public FacultyDTO change(FacultyDTO facultyDTO) {
        LOGGER.warn("Invoked method 'change' changes data about the faculty, data may be lost");
        Optional<Faculty> changedFaculty = facultyRepository.findById(facultyDTO.getId());
        if (changedFaculty.isPresent()) {
            Faculty faculty = changedFaculty.get();
            LOGGER.debug("The faculty before change: {}", faculty);
            changedFaculty.map(f -> {
                f.setName(facultyDTO.getName());
                f.setColor(facultyDTO.getColor());
                facultyRepository.save(f);
                return f;
            });
            LOGGER.debug("The faculty after change: {}", faculty);
            return mapToDTO(faculty);
        } else {
            LOGGER.error("FacultyNotFoundException. There is no such faculty");
            throw new FacultyNotFoundException();
        }
    }

    @Override
    public FacultyDTO deleteById(long id) {
        LOGGER.warn("Invoked method 'deleteById' delete data about the faculty with 'id = {}', data may be lost", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isPresent()) {
            Faculty deletedFaculty = faculty.get();
            facultyRepository.delete(deletedFaculty);
            LOGGER.debug("Faculty with 'id = {}' successfully deleted", id);
            return mapToDTO(deletedFaculty);
        } else {
            LOGGER.error("FacultyNotFoundException. There is no faculty with 'id = {}'", id);
            throw new FacultyNotFoundException();
        }
    }
}
