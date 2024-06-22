package com.example.pdfboxpoc.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PdfBoxPocService {
    private final PdfTemplateService pdfTemplateService;

    public PdfBoxPocService(PdfTemplateService pdfTemplateService) {
        this.pdfTemplateService = pdfTemplateService;
    }

    public Resource loadPdf() throws IOException {
        try (
            PDDocument template = pdfTemplateService.loadTemplate();
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
                contentStream.setFont(pdfTemplateService.loadCordiaFont(newDoc), 14);

                Map<String, String> dataMap = getData();
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if ("FreeText".equals(annotation.getSubtype())
                        && dataMap.containsKey(annotation.getContents())) {
                        // replace annotation with text.
                        String data = dataMap.get(annotation.getContents());
                        PDRectangle rectangle = annotation.getRectangle();
                        showTextAt(contentStream, data, rectangle.getLowerLeftX(), rectangle.getLowerLeftY());
                    }
                }
            }
            page.setAnnotations(Collections.emptyList()); // Delete all annotation.

            newDoc.save(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    public Map<String, String> getData() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("name", "นาย ทดสอบ นามสกุล");
        dataMap.put("age", "25");
        dataMap.put("address", "115");
        dataMap.put("street", "บำรุงสุข");
        dataMap.put("district", "ดินแดง");
        dataMap.put("sub_district", "ดินเหลือง");
        dataMap.put("province", "กรุงเทพ");
        dataMap.put("zip_code", "10200");
        dataMap.put("score1", "5");
        dataMap.put("score2", "4");
        dataMap.put("score3", "5");
        dataMap.put("score4", "3");
        dataMap.put("sign_name", "ทดสอบ นามสกุล");
        return dataMap;
    }

    public void showTextAt(PDPageContentStream contentStream, String text, float tx, float ty)
        throws IOException {
        contentStream.beginText();
        contentStream.setTextRise(1);
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
        contentStream.endText();
    }
}
