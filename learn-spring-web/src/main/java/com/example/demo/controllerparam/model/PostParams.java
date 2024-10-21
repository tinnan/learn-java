package com.example.demo.controllerparam.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PostParams {

    @NotBlank
    private String name;
    @NotNull
    private Integer age;
}
