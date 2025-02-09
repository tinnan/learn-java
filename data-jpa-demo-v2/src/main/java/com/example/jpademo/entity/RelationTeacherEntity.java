package com.example.jpademo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "relation_teacher")
@Entity(name = "relation_teacher")
public class RelationTeacherEntity {

    @Id
    private Integer id;
    private String name;
}
