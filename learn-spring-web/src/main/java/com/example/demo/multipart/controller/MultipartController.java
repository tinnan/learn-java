package com.example.demo.multipart.controller;

import com.example.demo.multipart.model.MultipartFileUploadRequest;
import com.example.demo.multipart.model.MultipartFileUploadResponse;
import com.example.demo.multipart.model.MultipartResponse;
import com.example.demo.multipart.service.MultipartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/multipart")
@RestController
@RequiredArgsConstructor
public class MultipartController {

    private final MultipartService multipartService;

    @GetMapping
    public ResponseEntity<MultiValueMap<String, HttpEntity<?>>> get() throws JsonProcessingException {
        MultiValueMap<String, HttpEntity<?>> map = new LinkedMultiValueMap<>();

        // --- JSON BODY ---
        MultiValueMap<String, String> jsonHeaders = new LinkedMultiValueMap<>();
        jsonHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.json\"");
        jsonHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        MultipartResponse model = new MultipartResponse("TI-900", "Terminator Robot T900");
        HttpEntity<MultipartResponse> jsonHttpEntity = new HttpEntity<>(model, jsonHeaders);
        map.add("json", jsonHttpEntity);

        // --- RESOURCE = FILE---
        String textData = multipartService.generateTextData();
        MultiValueMap<String, String> resourceHeaders = new LinkedMultiValueMap<>();
        resourceHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\"");
        resourceHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        HttpEntity<String> resourceHttpEntity = new HttpEntity<>(textData, resourceHeaders);
        map.add("csv", resourceHttpEntity);

        return ResponseEntity.ok().contentType(MediaType.MULTIPART_MIXED)
            .body(map);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<MultipartFileUploadResponse> uploadFile(MultipartFileUploadRequest request) {

        log.info("Upload data: {}", request);
        log.info("Multipart file name: {} ({})", request.getFile().getName(), request.getFile().getContentType());
        return ResponseEntity.ok(MultipartFileUploadResponse.builder().uploadedFileName(request.getFile().getName())
            .uploadedContentType(request.getFile().getContentType()).build());
    }
}
