package com.example.demo.controller;

import com.example.demo.controller.exception.UserNotFoundException;
import com.example.demo.data.User;
import com.example.demo.domain.UserResponse;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Moved to GlobalExceptionHandler.
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException unfe) {
//        List<String> errors = Collections.singletonList(unfe.getMessage());
//        return new ResponseEntity<>(new ApiError(errors), HttpStatus.NOT_FOUND);
//    }
}
