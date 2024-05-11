package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.Period;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
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
    @Transient
    private int daysSinceJoin;

    public int getDaysSinceJoin() {
        return Period.between(joinDate, LocalDate.now())
                .getDays();
    }
}
