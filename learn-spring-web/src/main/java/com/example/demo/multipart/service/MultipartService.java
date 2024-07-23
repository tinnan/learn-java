package com.example.demo.multipart.service;

import com.example.demo.multipart.client.MultipartClient;
import com.example.demo.multipart.model.MultipartFileUploadRequest;
import com.example.demo.multipart.model.MultipartFileUploadResponse;
import com.example.demo.multipart.model.MultipartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultipartService {

    private final MultipartClient multipartClient;

    public String generateTextData() throws JsonProcessingException {
        List<MultipartResponse> responseList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            responseList.add(new MultipartResponse("TOY-" + i, "Toy model " + i));
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(responseList);
    }

    public MultipartFileUploadResponse uploadFile(MultipartFileUploadRequest request) {
        ResponseEntity<MultipartFileUploadResponse> response = multipartClient.uploadToUpstream(request);
        return response.getBody();
    }
}
