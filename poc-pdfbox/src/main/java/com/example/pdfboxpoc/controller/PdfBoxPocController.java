package com.example.pdfboxpoc.controller;

import com.example.pdfboxpoc.service.PdfBoxPocService;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/pdf")
@RestController
public class PdfBoxPocController {
    private final PdfBoxPocService pdfBoxPocService;

    public PdfBoxPocController(PdfBoxPocService pdfBoxPocService) {
        this.pdfBoxPocService = pdfBoxPocService;
    }

    @GetMapping
    public ResponseEntity<Resource> loadPdf() throws IOException {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfBoxPocService.loadPdf());
    }
}
