package com.example.demorelations.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Integer teacherId;
    @ManyToOne
    @JoinColumn(
            name = "teacherId", // Join key column name on the table "course"
            referencedColumnName = "id", // which referenced to ID column name of table "teacher".
            insertable = false,
            updatable = false
    )
    private Teacher teacher;

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
