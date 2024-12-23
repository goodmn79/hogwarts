package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.dto.AvatarDTO;
import ru.hogwarts.school.service.AvatarService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AvatarController.class)
class AvatarControllerTest {
    private static final long ID = 1L;

    private static final String URL = "http://localhost:8080/avatars/{studentId}";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    private AvatarDTO testAvatarDTO;

    @Test
    void testUploadAvatar() throws Exception {
        long studentId = 1L;
        MockMultipartFile mockFile = new MockMultipartFile(
                "avatar", "test_avatar.png", MediaType.MULTIPART_FORM_DATA_VALUE, "test image content".getBytes());
        System.out.println(URL);
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
                .setName("test_avatar.png")
                .setPath(filePath)
                .setSize(data.length)
                .setMediaType(MediaType.IMAGE_PNG_VALUE)
                .setData(data);
        when(avatarService.getAvatar(ID)).thenReturn(testAvatarDTO);

        ResultActions perform = mockMvc.perform(get(URL, ID));

        perform
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assert response.getStatus() == HttpServletResponse.SC_OK;
                    assert Objects.equals(response.getContentType(), MediaType.IMAGE_PNG_VALUE);
                    assert response.getContentLength() == testAvatarDTO.getData().length;
                })
                .andExpect(content().bytes(testAvatarDTO.getData()))
                .andExpect(header().string("Content-Length", String.valueOf(testAvatarDTO.getSize())));

    }

    @Test
    void testDownloadAvatar() throws Exception {
        testAvatarDTO = new AvatarDTO()
                .setId(ID)
                .setName("test_avatar.png")
                .setPath("/avatars/test_avatar.png")
                .setSize("test image content".getBytes().length)
                .setMediaType(MediaType.IMAGE_PNG_VALUE)
                .setData("test image content".getBytes());
        when(avatarService.getAvatar(ID)).thenReturn(testAvatarDTO);

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