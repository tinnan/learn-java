package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String email;
    private LocalDate joinDate;
    @Transient
    private int daysSinceJoined;

    public User(String username, String email, LocalDate joinDate) {
        this.username = username;
        this.email = email;
        this.joinDate = joinDate;
    }

    public int getDaysSinceJoined() {
        return Period.between(this.joinDate, LocalDate.now())
                .getDays();
    }
}
