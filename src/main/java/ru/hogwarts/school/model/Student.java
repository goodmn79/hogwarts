package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private int age;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @ToString.Exclude
    private Faculty faculty;

    @OneToOne
    @JoinColumn(name = "avatar_id")
    @ToString.Exclude
    private Avatar avatar;

}
