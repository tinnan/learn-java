package com.example.demo.dao;

import com.example.demo.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@TestPropertySource(properties = {
        // Double quote identifiers to avoid SQL syntax error in case reserved word is used.
        "spring.jpa.properties.hibernate.globally_quoted_identifiers=true"
})
class UserRepositoryTest {
    private final LocalDate localDateJoined = LocalDate.of(2024, Month.APRIL, 1);
    private final LocalDate localDateNow = LocalDate.of(2024, Month.APRIL, 10);
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void givenNewUser_whenSave_thenRecordIsCreated() {
        try (MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now)
                    .thenReturn(localDateNow);
            // LocalDate.from(date) will be called in Period.between(start, end)
            mockedStatic.when(() -> LocalDate.from(any()))
                    .thenReturn(localDateNow);

            User newUser = new User("johnd", "john.d@gmail.com", localDateJoined);

            String id = userRepository.save(newUser)
                    .getId();

            Optional<User> savedUser = userRepository.findById(id);
            assertTrue(savedUser.isPresent());
            User expectedUser = new User(newUser.getUsername(), newUser.getEmail(), newUser.getJoinDate());
            expectedUser.setId(id);
            assertEquals(expectedUser, savedUser.get());
            assertEquals(9, savedUser.get()
                    .getDaysSinceJoined());
        }
    }

    @Test
    public void givenExistingUser_whenUpdate_thenRecordedIsUpdated() {
        User existingUser = userRepository.save(new User("johnd", "john.d@gmail.com", localDateJoined));

        existingUser.setEmail("johny@hotmail.com");
        userRepository.save(existingUser);

        Optional<User> updatedUser = userRepository.findById(existingUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("johny@hotmail.com", updatedUser.get().getEmail());
    }
}