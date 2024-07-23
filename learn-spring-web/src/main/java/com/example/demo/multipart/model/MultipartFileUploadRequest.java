package com.example.demo.multipart.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MultipartFileUploadRequest {

    private MultipartFile file;
}
