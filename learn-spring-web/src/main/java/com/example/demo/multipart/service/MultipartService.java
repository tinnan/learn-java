package com.example.demo.multipart.service;

import com.example.demo.multipart.model.MultipartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MultipartService {
    public String generateTextData() throws JsonProcessingException {
        List<MultipartResponse> responseList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            responseList.add(new MultipartResponse("TOY-" + i, "Toy model " + i));
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(responseList);
    }
}
