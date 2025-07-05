package com.example.demo.controllerparam.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DemoRequest {

    private List<String> ids;
}
