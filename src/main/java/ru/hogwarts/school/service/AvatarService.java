package ru.hogwarts.school.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.Files.notExists;
import static java.nio.file.Files.write;
import static ru.hogwarts.school.mapper.AvatarMapper.mapToDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class AvatarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatars.folder.path}")
    private String avatarDir;

    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final EntityManager entityManager;

    public void addAvatar(long studentId, MultipartFile multipartFile) throws IOException {
        LOGGER.info("Invoked method 'addAvatar'");
        if (studentService.getById(studentId) == null) {
            LOGGER.error("StudentNotFoundException. Students with 'id = {}' not found in DB 'Hogwarts'", studentId);
            throw new StudentNotFoundException();
        }
        Student student = entityManager.getReference(Student.class, studentId);
        String file = avatarDir + getFileName(studentId, Objects.requireNonNull(multipartFile.getOriginalFilename()));
        Path path = Path.of(file);
        LOGGER.debug("Path of avatar: {}", path);
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar())
                .setPath(file)
                .setSize((int) multipartFile.getSize())
                .setMediaType(multipartFile.getContentType())
                .setData(multipartFile.getBytes())
                .setStudent(student);
        avatarRepository.save(avatar);
        Path avatarDirPath = Path.of(avatarDir);
        LOGGER.debug("Path of avatar directory: {}", avatarDirPath);
        if (Files.notExists(avatarDirPath)) Files.createDirectory(avatarDirPath);
        write(path, multipartFile.getBytes());
    }

    public Collection<AvatarDTO> getAvatars(int numOfPage, int size) {
        LOGGER.info("Invoked method 'getAvatars', to get a page-by-page list");
        PageRequest page = PageRequest.of((numOfPage - 1), size);
        Collection<Avatar> avatars = avatarRepository.findAll(page).getContent();
        if (avatars.isEmpty()) {
            LOGGER.error("AvatarNotFoundException. Avatars not found in DB'Hogwarts'");
            throw new AvatarNotFoundException();
        }
        LOGGER.debug("The list of avatars size = {}", avatars.size());
        return mapToDTO(avatars);
    }

    public AvatarDTO getAvatar(long studentId) {
        LOGGER.info("Invoked method 'getAvatar', to get avatar of student with 'id = {}'", studentId);
        Optional<Avatar> foundAvatar = avatarRepository.findByStudentId(studentId);
        if (foundAvatar.isPresent()) {
            Avatar avatar = foundAvatar.get();
            LOGGER.debug("The avatar: {} was received", avatar);
            return mapToDTO(avatar);
        } else {
            LOGGER.error("AvatarNotFoundException. Avatar of student with 'id = {}' not found in DB'Hogwarts'", studentId);
            throw new AvatarNotFoundException();
        }
    }

    public void deleteAvatar(long id) throws IOException {
        LOGGER.warn("Invoked method 'deleteAvatar' delete data about the avatar with 'id = {}'", id);
        Optional<Avatar> avatar = avatarRepository.findById(id);
        if (avatar.isPresent()) {
            Avatar deletedAvatar = avatar.get();
            Path avatarPath = Path.of(deletedAvatar.getPath());
            Files.deleteIfExists(avatarPath);
            if (notExists(avatarPath)) avatarRepository.delete(deletedAvatar);
            LOGGER.debug("Avatar with 'id = {}' successfully deleted", id);
        } else {
            LOGGER.error("AvatarNotFoundException. There is no such avatar with 'id = {}'", id);
            throw new AvatarNotFoundException();
        }

    }

    private String getFileName(long studentId, String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return studentId + extension;
    }
}
