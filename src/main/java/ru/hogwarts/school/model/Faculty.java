package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collection;

@Entity
@Data
@Accessors(chain = true)
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "faculty")
    @ToString.Exclude
    private Collection<Student> students;

}
