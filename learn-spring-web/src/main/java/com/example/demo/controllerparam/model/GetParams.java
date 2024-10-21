package com.example.demo.controllerparam.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetParams {

    @NotBlank
    private String name;
    @NotNull
    private Integer age;
}
