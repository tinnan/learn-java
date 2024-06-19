package com.example.pdfboxpoc.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
    public PDFont loadCordiaFont(PDDocument document) throws IOException {
        Path fontFilePath = Paths.get("src/main/resources/font/cordia.ttf");
        return PDType0Font.load(document, fontFilePath.toFile());
    }

    public PDDocument loadTemplate() throws IOException {
        return Loader.loadPDF(Paths.get("src/main/resources/template/AL_contract.pdf").toFile());
    }
}
