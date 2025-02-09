package com.example.jpademo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "relation_course")
@Entity(name = "relation_course")
public class RelationCourseEntity {

    @Id
    private Integer id;
    private String code;
    private String name;
    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private RelationTeacherEntity teacher;
}
