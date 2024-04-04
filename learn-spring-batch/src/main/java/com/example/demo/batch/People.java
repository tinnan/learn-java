package com.example.demo.batch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class People {
    @Id
    @SequenceGenerator(name = "person_id_generator", sequenceName = "person_id_generator")
    @GeneratedValue(generator = "person_id_generator", strategy = GenerationType.SEQUENCE)
    private Long personId;
    private String firstName;
    private String lastName;

    public People(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
