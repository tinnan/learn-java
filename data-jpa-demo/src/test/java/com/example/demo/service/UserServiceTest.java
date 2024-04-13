package com.example.demo.service;

import com.example.demo.dao.UserRepository;
import com.example.demo.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void givenExistingUser_whenUpdateEmail_thenSuccess() {
        User existingUser = userService.createUser(new User("johnd", "john.d@gmail.com", LocalDate.of(2024, Month.APRIL,
                1), 2));

        final String newEmail = "johny@hotmail.com";
        User updatedResult = userService.updateUserEmail(existingUser.getId(), newEmail);
        assertNotNull(updatedResult);

        User updatedUser = userService.getUser(existingUser.getId());
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    public void givenNonExistingUser_whenUpdateEmail_thenNothingUpdated() {
        userService.createUser(new User("johnd", "john.d@gmail.com", LocalDate.of(2024, Month.APRIL,
                1), 2));

        final String newEmail = "johny@hotmail.com";
        User updatedResult = userService.updateUserEmail("ABC", newEmail);
        assertNull(updatedResult);
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public UserService userService(UserRepository userRepository, EntityManager entityManager) {
            return new UserService(userRepository, entityManager);
        }
    }
}