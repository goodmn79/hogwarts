package ru.hogwarts.school.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FacultyDTO {
    private long id;
    private String name;
    private String color;
}
