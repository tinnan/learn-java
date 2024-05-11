package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @SequenceGenerator(name = "fraud_id_generator", sequenceName = "fraud_id_generator")
    @GeneratedValue(generator = "fraud_id_generator", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String notifyToEmail;
    private String message;
}
