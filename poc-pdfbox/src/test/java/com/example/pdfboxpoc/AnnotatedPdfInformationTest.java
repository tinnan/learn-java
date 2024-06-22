package com.example.pdfboxpoc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AnnotatedPdfInformationTest {
    private static final Logger log = LoggerFactory.getLogger(AnnotatedPdfInformationTest.class);

    @Test
    void annotatedPdfInfo() throws IOException {
        try (PDDocument template = Loader.loadPDF(
            Paths.get("src/test/resources/template/annotated_template.pdf").toFile())) {
            PDPage page = template.getPage(0);
            log.info("Annotations");
            log.info("");
            for (PDAnnotation annotation : page.getAnnotations()) {
                log.info("Name: {}", annotation.getAnnotationName());
                log.info("Content: {}", annotation.getContents());
                log.info("Sub type: {}", annotation.getSubtype());
                log.info("Border: {}", annotation.getBorder());
                log.info("Annotation flag: {}", annotation.getAnnotationFlags());
                log.info("Optional content: {}", annotation.getOptionalContent());
                log.info("Rectangle (Lower X, Lower Y, Upper X, Upper Y): {}", annotation.getRectangle());
                log.info("Color: {}", annotation.getColor());
                log.info("Appearance state: {}", annotation.getAppearanceState());
                log.info("#------------");
            }
        }

        assertTrue(true);
    }
}
