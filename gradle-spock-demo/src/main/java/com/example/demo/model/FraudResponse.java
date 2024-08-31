package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudResponse {

    private boolean fraudster;
}
