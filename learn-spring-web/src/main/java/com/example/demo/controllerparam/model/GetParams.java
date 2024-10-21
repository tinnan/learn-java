package com.example.demo.controllerparam.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// ! For POJO for request params object, if there is no args constructor then there must be field setters.
// ! Otherwise, Spring will not bind request params to the POJO.
public class GetParams {

    @NotBlank
    private String name;
    @NotNull
    private Integer age;
}
