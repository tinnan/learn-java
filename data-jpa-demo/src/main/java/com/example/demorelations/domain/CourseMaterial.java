package com.example.demorelations.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterial {
    @Id
    @SequenceGenerator(name = "course_material_id_generator", sequenceName = "course_material_id_generator")
    @GeneratedValue(generator = "course_material_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String url;
    @OneToOne(optional = false)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Override
    public String toString() {
        return "CourseMaterial{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}