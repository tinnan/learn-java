package com.example.demo.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@AllArgsConstructor
@Getter
public class ContentNotAllowedException extends Exception {
    private List<ObjectError> errors;
}
