package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Avatar {
    @Id
    @GeneratedValue
    private long id;
    private String filePath;
    private int fileSize;
    private String mediaType;
    @Lob
    private byte[] data;
    @OneToOne
    private Student student;

}
