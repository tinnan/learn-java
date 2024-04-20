package com.example.demorelations.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @SequenceGenerator(name = "student_id_generator", sequenceName = "student_id_generator")
    @GeneratedValue(generator = "student_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private boolean wantsNewsLetter;
    // Gender will be persisted as enumerated type.
    @Enumerated(EnumType.STRING)
    private Gender gender;
    // See field "students" in Course entity.
    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    public enum Gender {
        MALE,
        FEMALE
    }
}
