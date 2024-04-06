package com.example.demo.batch.tut1.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Profile({ "tut1", "api" })
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
