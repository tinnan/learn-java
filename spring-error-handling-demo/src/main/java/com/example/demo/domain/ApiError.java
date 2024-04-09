package com.example.demo.domain;

import java.util.List;

public record ApiError(List<String> errors) {
}
