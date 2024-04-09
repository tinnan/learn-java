package com.example.demo.config;

import com.example.demo.data.User;
import com.example.demo.data.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DataInitConfig implements CommandLineRunner {
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.saveAll(List.of(
                new User(null, "john_d", "John Doe", LocalDate.of(2005, Month.DECEMBER, 15)),
                new User(null, "jame_s", "James G.", LocalDate.of(2021, Month.FEBRUARY, 3))
        ));
    }
}
