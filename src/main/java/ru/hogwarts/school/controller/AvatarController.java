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

@RestController
@RequestMapping("/avatars/{studentId}")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable long studentId, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.isEmpty()) return ResponseEntity.badRequest().body("file is empty");
        if (avatar.getSize() > 1024 * 300) return ResponseEntity.badRequest().body("file size is too large");
        service.upload(studentId, avatar);
        return ResponseEntity.ok().body("avatar successfully added");
    }

    @GetMapping
    public void getAvatarByStudentId(@PathVariable long studentId, HttpServletResponse response) throws IOException {
        AvatarDTO avatarDTO = service.getFromDB(studentId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(avatarDTO.getMediaType());
        response.setContentLength(avatarDTO.getFileSize());
        try (OutputStream os = response.getOutputStream()) {
            Files.copy(Path.of(avatarDTO.getFilePath()), os);
        }
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable long studentId) {
        AvatarDTO avatarDTO = service.getFromDB(studentId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", avatarDTO.getName(avatarDTO.getFilePath()));
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatarDTO.getData());
    }
}
