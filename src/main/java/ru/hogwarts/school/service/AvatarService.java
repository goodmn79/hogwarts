package ru.hogwarts.school.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.FileNotExistException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.Files.write;
import static ru.hogwarts.school.mapper.AvatarMapper.mapToDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class AvatarService {
    @Value("${avatars.folder.path}")
    private String avatarDir;

    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final EntityManager entityManager;

    public void upload(long studentId, MultipartFile multipartFile) throws IOException {
        if (studentService.getById(studentId) == null) throw new StudentNotFoundException();
        Student student = entityManager.getReference(Student.class, studentId);
        String file = avatarDir + getFileName(studentId, Objects.requireNonNull(multipartFile.getOriginalFilename()));
        Path path = Path.of(file);
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar())
                .setFilePath(file)
                .setFileSize((int) multipartFile.getSize())
                .setMediaType(multipartFile.getContentType())
                .setData(multipartFile.getBytes())
                .setStudent(student);
        avatarRepository.save(avatar);
        if (Files.notExists(Path.of(avatarDir))) Files.createDirectory(Path.of(avatarDir));
        write(path, multipartFile.getBytes());
    }

    public AvatarDTO getFromDB(long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseThrow(AvatarNotFoundException::new);
        Path avatarPath = Path.of(avatar.getFilePath());
        if (Files.notExists(avatarPath)) throw new FileNotExistException();
        return mapToDTO(avatar);
    }

    private String getFileName(long studentId, String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return studentId + extension;
    }
}
