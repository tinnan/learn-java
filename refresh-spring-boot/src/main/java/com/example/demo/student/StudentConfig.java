package com.example.demo.student;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
        return args -> {
            Student tin = new Student("Tin", "tin@gmail.com", LocalDate.of(1989, Month.MARCH, 7));
            Student john = new Student("John", "john@gmail.com", LocalDate.of(2000, Month.JANUARY, 25));

            studentRepository.saveAll(List.of(tin, john));
        };
    }
}
