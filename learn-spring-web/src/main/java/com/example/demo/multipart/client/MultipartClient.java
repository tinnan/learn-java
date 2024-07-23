package com.example.demo.multipart.client;

import com.example.demo.multipart.model.MultipartFileUploadRequest;
import com.example.demo.multipart.model.MultipartFileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "multipart-client", url = "${multipart-client.url}")
public interface MultipartClient {

    @PostMapping(value = "/api/v1/multipart/file/upload/upstream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MultipartFileUploadResponse> uploadToUpstream(MultipartFileUploadRequest request);
}
