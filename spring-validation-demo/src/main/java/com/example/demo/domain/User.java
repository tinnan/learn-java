package com.example.demo.domain;

import com.example.demo.validation.constraints.Between;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "{validation.user.notBlank}")
    private String name;

    @NotBlank(message = "{validation.email.notBlank}")
    private String email;

    @Between(min = 10, max = 60, message = "{validation.age.between}")
    private Integer age;
}
