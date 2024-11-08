package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String path;
    private int size;
    private String mediaType;
    @Transient
    private byte[] data;
    @OneToOne
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    private Student student;

}
