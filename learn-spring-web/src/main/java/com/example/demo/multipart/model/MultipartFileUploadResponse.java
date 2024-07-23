package com.example.demo.multipart.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultipartFileUploadResponse {

    private String uploadedFileName;
    private String uploadedContentType;
}
