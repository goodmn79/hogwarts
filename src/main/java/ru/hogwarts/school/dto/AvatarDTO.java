package ru.hogwarts.school.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvatarDTO {
    private long id;
    private String fileName;
    private String filePath;
    private int fileSize;
    private String mediaType;
    private byte[] data;

    public String getName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
}
