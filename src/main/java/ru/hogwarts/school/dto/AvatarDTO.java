package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvatarDTO {
    private long id;
    private String name;
    private String path;
    private int size;
    private String mediaType;
    @JsonIgnore
    private byte[] data;
}
