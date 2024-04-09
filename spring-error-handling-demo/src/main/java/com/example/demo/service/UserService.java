package com.example.demo.service;

import com.example.demo.data.User;
import com.example.demo.data.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String username) {
        return userRepository.findOneByUsername(username).orElse(null);
    }
}
