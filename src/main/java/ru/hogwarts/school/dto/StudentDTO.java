package ru.hogwarts.school.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StudentDTO {
    private long id;
    private String name;
    private int age;
    private long facultyId;
}
