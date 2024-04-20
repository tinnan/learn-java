package com.example.demorelations.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;
    private String title;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "teacher_id", // Join key column name on the table "course"
            referencedColumnName = "id" // which referenced to ID column name of table "teacher".
    )
    private Teacher teacher;
    @OneToOne(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private CourseMaterial courseMaterial;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    // JoinTable will set up a table that represents relationship between Student and Course.
    // The table will contain 2 columns named: course_id (owning side), student_id (referencing side).
    @JoinTable(name = "students_courses",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id")
    )
    private List<Student> students;

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
