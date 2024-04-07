package com.example.demo.batch.mvc.model;

public record JobSummary(Long instanceId, Long executionId, String endTime, String status) {
}
