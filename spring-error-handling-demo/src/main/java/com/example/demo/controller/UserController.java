package com.example.demo.controller;

import com.example.demo.data.User;
import com.example.demo.controller.exception.UserNotFoundException;
import com.example.demo.domain.ApiError;
import com.example.demo.domain.UserResponse;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("username") String username) throws UserNotFoundException {
        User user = userService.getUser(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getDisplayName(), user.getJoinDate()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException unfe) {
        List<String> errors = Collections.singletonList(unfe.getMessage());
        return new ResponseEntity<>(new ApiError(errors), HttpStatus.NOT_FOUND);
    }
}
