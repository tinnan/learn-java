package com.example.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@AllArgsConstructor
public class Customer {
    @Id
    @SequenceGenerator(name = "customer_id_generator", sequenceName = "customer_id_generator")
    @GeneratedValue(generator = "customer_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    @Email
    private String email;
    private LocalDate joinDate;

    public int getDaysSinceJoin() {
        return Period.between(joinDate, LocalDate.now())
                .getDays();
    }
}
