package com.example.demorelations.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
