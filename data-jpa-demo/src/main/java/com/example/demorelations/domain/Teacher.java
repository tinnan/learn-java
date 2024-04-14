package com.example.demorelations.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Teacher {
    @Id
    @SequenceGenerator(name = "teacher_id_sequence", sequenceName = "teacher_id_sequence")
    @GeneratedValue(generator = "teacher_id_sequence", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String firstName;
    private String lastName;
    // Name of field that owns the relation (field inside associated entity, which is field "teacher" in "Course"
    // entity in this case).
    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.ALL})
    // Map primary key of associated table to key value of Map. Teacher class is the "owning side" of one-to-many
    // relationship. Course class is called "referencing" side.
    @MapKey
    private Map<String, Course> courses;

    public Teacher(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
