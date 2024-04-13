package com.example.demo.service;

import com.example.demo.dao.UserRepository;
import com.example.demo.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserEmail(String id, String email) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        user.setEmail(email);

        return user;
    }

    public User getUser(String id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public Collection<User> listUsers() {
        return userRepository.findAll();
    }
}
