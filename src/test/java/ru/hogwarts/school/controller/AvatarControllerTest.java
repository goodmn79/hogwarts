package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.dto.AvatarDTO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvatarController.class)
class AvatarControllerTest {
    private static final String URL = "/avatars/{studentId}";
    private static final long ID = 1L;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    private AvatarDTO testAvatarDTO;

    @Test
    void testUploadAvatar() throws Exception {
        long studentId = 1L;
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "test_avatar.png", MediaType.MULTIPART_FORM_DATA_VALUE, "test image content".getBytes());

        ResultActions perform = mockMvc.perform(multipart(URL, studentId)
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("avatar successfully added"))
                .andDo(print());
    }

    @Test
    void testGetAvatarByStudentId() throws Exception {
        String filePath = "src/test/java/resources/test_avatar.png";
        byte[] data = Files.readAllBytes(Path.of(filePath));
        testAvatarDTO = new AvatarDTO()
                .setId(ID)
                .setFileName("test_avatar.png")
                .setFilePath(filePath)
                .setFileSize(data.length)
                .setMediaType(MediaType.IMAGE_PNG_VALUE)
                .setData(data);
        when(avatarService.getFromDB(ID)).thenReturn(testAvatarDTO);

        ResultActions perform = mockMvc.perform(get(URL, ID));

        perform
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assert response.getStatus() == HttpServletResponse.SC_OK;
                    assert Objects.equals(response.getContentType(), MediaType.IMAGE_PNG_VALUE);
                    assert response.getContentLength() == testAvatarDTO.getData().length;
                })
                .andExpect(content().bytes(testAvatarDTO.getData()))
                .andExpect(header().string("Content-Length", String.valueOf(testAvatarDTO.getFileSize())));

    }

    @Test
    void testDownloadAvatar() throws Exception {
        testAvatarDTO = new AvatarDTO()
                .setId(ID)
                .setFileName("test_avatar.png")
                .setFilePath("/avatars/test_avatar.png")
                .setFileSize("test image content".getBytes().length)
                .setMediaType(MediaType.IMAGE_PNG_VALUE)
                .setData("test image content".getBytes());
        when(avatarService.getFromDB(ID)).thenReturn(testAvatarDTO);

        ResultActions perform = mockMvc.perform(get(URL + "/download", ID)
                .content(testAvatarDTO.getData())
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        perform
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().bytes(testAvatarDTO.getData()))
                .andDo(print());
    }
}