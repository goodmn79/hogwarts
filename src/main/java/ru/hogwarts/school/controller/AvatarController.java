package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("/avatars")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable long studentId, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.isEmpty()) return ResponseEntity.badRequest().body("file is empty");
        if (avatar.getSize() > 1024 * 300) return ResponseEntity.badRequest().body("file size is too large");
        avatarService.addAvatar(studentId, avatar);
        return ResponseEntity.ok().body("avatar successfully added");
    }

    @GetMapping("/{studentId}")
    public void getAvatarByStudentId(@PathVariable long studentId, HttpServletResponse response) throws IOException {
        AvatarDTO avatarDTO = avatarService.getAvatar(studentId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(avatarDTO.getMediaType());
        response.setContentLength(avatarDTO.getSize());
        try (OutputStream out = response.getOutputStream()) {
            Files.copy(Path.of(avatarDTO.getPath()), out);
        }
    }

    @GetMapping
    public Collection<AvatarDTO> getPageOfAvatars(int numOfPage, int size) {
        return avatarService.getAvatars(numOfPage, size);
    }

    @GetMapping(value = "/{studentId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable long studentId) throws IOException {
        AvatarDTO avatarDTO = avatarService.getAvatar(studentId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", avatarDTO.getName());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatarDTO.getData());
    }
}
