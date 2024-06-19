package com.example.pdfboxpoc.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class PdfBoxPocService {
    private final PdfService pdfService;

    public PdfBoxPocService(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    public Resource loadPdf() throws IOException {
        try (
            PDDocument template = pdfService.loadTemplate();
            PDDocument newDoc = new PDDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            for (PDPage page : template.getPages()) {
                COSDictionary pageDict = page.getCOSObject();
                COSDictionary newPageDict = new COSDictionary(pageDict);
                PDPage newPage = new PDPage(newPageDict);
                newDoc.addPage(newPage);
            }

            PDPage page = newDoc.getPage(0);
            try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, page, AppendMode.APPEND, false)) {
                contentStream.setFont(pdfService.loadCordiaFont(newDoc), 10);

                showTextAt(contentStream, "9912039124", 330, 640);
                showTextAt(contentStream, "19", 230, 610);
                showTextAt(contentStream, "มิถุนายน", 265, 610);
                showTextAt(contentStream, "2567", 310, 610);

                showTextAt(contentStream, "นาย ทดสอบ นามสกุล", 120, 590);
                showTextAt(contentStream, "14", 295, 590);
                showTextAt(contentStream, "เล้าหมู", 360, 590);

                showTextAt(contentStream, "บำรุงสุข", 72, 577);
                showTextAt(contentStream, "ดินแดง", 160, 577);
                showTextAt(contentStream, "ดินเหลือง", 230, 577);
                showTextAt(contentStream, "กทม", 295, 577);
            }

            newDoc.save(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    public void showTextAt(PDPageContentStream contentStream, String text, float tx, float ty)
        throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
        contentStream.endText();
    }
}
