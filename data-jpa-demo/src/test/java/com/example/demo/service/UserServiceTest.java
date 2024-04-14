package com.example.demo.service;

import com.example.demo.dao.UserRepository;
import com.example.demo.domain.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("/test-data/createUser.sql")
    public void givenExistingUser_whenUpdateEmail_thenSuccess() {
        String existingUserId = "738332fd-1deb-46c5-a633-89d6572aadbf";

        final String newEmail = "johny@hotmail.com";
        User updatedResult = userService.updateUserEmail(existingUserId, newEmail);
        assertNotNull(updatedResult);

        User updatedUser = userService.getUser(existingUserId);
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    @Sql("/test-data/createUser.sql")
    public void givenExistingUser_whenUpdateEmailDuplicated_thenFail() {
        String existingUserId = "738332fd-1deb-46c5-a633-89d6572aadbf";

        final String newEmail = "jane.d@gmail.com";
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.updateUserEmail(existingUserId, newEmail);
            userRepository.flush();
        });
    }

    @Test
    @Sql("/test-data/createUser.sql")
    public void givenNonExistingUser_whenUpdateEmail_thenNothingUpdated() {
        final String newEmail = "johny@hotmail.com";
        User updatedResult = userService.updateUserEmail("ABC", newEmail);
        userRepository.flush();
        assertNull(updatedResult);
    }

    // todo: use DBUnit in user test.

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public UserService userService(UserRepository userRepository, EntityManager entityManager) {
            return new UserService(userRepository, entityManager);
        }
    }
}