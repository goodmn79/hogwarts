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
    private String path;
    private int size;
    private String mediaType;
    @Transient
    private byte[] data;
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
