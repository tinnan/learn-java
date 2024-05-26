package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.validation.marker.AllUser;
import com.example.demo.validation.marker.RegularUser;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping
    ResponseEntity<String> addUser(@Validated({AllUser.class, RegularUser.class}) @RequestBody User user) {
        // When the target argument fails to pass the validation, Spring Boot throws a MethodArgumentNotValidException exception.
        userRepository.save(user);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"User is valid.\"}");
    }

    @GetMapping
    ResponseEntity<String> getUser(@RequestParam("email") String email) {
        String user = userService.getUser(email);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(user);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String field = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            errors.put(field, errorMessage);
        });
        return errors;
    }
}
