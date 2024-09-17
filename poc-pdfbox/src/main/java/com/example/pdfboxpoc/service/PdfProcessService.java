package com.example.pdfboxpoc.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class PdfProcessService {

    private static final double INCH_IN_MM = 25.4;
    private static final double MARGIN_MM = INCH_IN_MM;
    private static final int DPI = 72;
    private static final int A4_WIDTH_MM = 210;
    private static final int A4_HEIGHT_MM = 297;

    /**
     * Split 1 PDF to multiple PDF files. 1 page = 1 file.
     */
    @SneakyThrows
    public void split() {
        logMemUsage("Split started");
        Path outDir = Path.of("./build/", "pdf-split");
        prepOutputDirectory(outDir);
        ClassPathResource classPathResource = new ClassPathResource("input/split/document.pdf");
        try (PDDocument document = Loader.loadPDF(classPathResource.getFile())) {
            logMemUsage("PDF loaded");
            Splitter splitter = new Splitter(); // By default, split each page into new documents.
            List<PDDocument> splitDocuments = splitter.split(document);
            logMemUsage("PDF split");
            for (int i = 0; i < splitDocuments.size(); i += 1) {
                int seq = i + 1;
                String fileName = String.format("document_%02d.pdf", seq);
                try (PDDocument splitDocument = splitDocuments.get(i)) {
                    splitDocument.save(outDir.resolve(fileName).toFile());
                    logMemUsage("PDF save");
                }
            }
        }
        logMemUsage("Split ended");
    }

    /**
     * Merge PDF, JPEG files into 1 PDF file. 1 JPEG file = 1 page.
     */
    @SneakyThrows
    public void merge() {
        logMemUsage("Merge started");
        Path outDir = Path.of("./build", "pdf-merge");
        prepOutputDirectory(outDir);
        ClassPathResource classPathResource = new ClassPathResource("input/merge");
        File inputDir = classPathResource.getFile();
        File[] inputFiles = Arrays.stream(Objects.requireNonNullElse(inputDir.listFiles(), new File[]{}))
            .sorted(this::compareFileName)
            .toArray(File[]::new);
        logMemUsage("Input file indexing/sorting");

        List<PDDocument> mergingDocList = new ArrayList<>();
        try (
            PDDocument destination = new PDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            logMemUsage("New PDFMergerUtility");
            for (File input : inputFiles) {
                String inputFileName = input.getName();
                String extension = inputFileName.substring(inputFileName.lastIndexOf('.') + 1);
                if (isJpeg(extension)) {
                    PDDocument jpegDoc = new PDDocument();
                    jpegToPdf(jpegDoc, input);
                    pdfMergerUtility.appendDocument(destination, jpegDoc);
                    mergingDocList.add(jpegDoc);
                    logMemUsage("Append JPEG");
                } else {
                    try (FileInputStream ins = new FileInputStream(input)) {
                        PDDocument doc = Loader.loadPDF(ins.readAllBytes());
                        pdfMergerUtility.appendDocument(destination, doc);
                        mergingDocList.add(doc);
                        logMemUsage("Append PDF");
                    }
                }
            }

            destination.save(out);
            logMemUsage("Save PDF");
            Files.write(outDir.resolve("document.pdf"), out.toByteArray(), StandardOpenOption.CREATE_NEW);
            logMemUsage("Write PDF");
        } finally {
            for (PDDocument mergingDoc : mergingDocList) {
                mergingDoc.close();
            }
        }
        logMemUsage("Merge ended");
    }

    private void jpegToPdf(PDDocument doc, File jpegFile) throws IOException {
        int a4WidthPx = (int) (A4_WIDTH_MM / INCH_IN_MM * DPI);
        int a4HeightPx = (int) (A4_HEIGHT_MM / INCH_IN_MM * DPI);
        PDPage page = new PDPage(new PDRectangle(0, 0, a4WidthPx, a4HeightPx));
        try (
            FileInputStream ins = new FileInputStream(jpegFile);
            PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false)
        ) {
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, ins.readAllBytes(), null);

            // Scale image to fit A4 paper size with 1 inch (25.4 mm) margin on each side.
            // Number "2" is number of margin on each orientation (2 top/bottom, 2 left/right).
            int effectiveWidth = A4_WIDTH_MM - 2 * (int) MARGIN_MM;
            int effectiveHeight = A4_HEIGHT_MM - 2 * (int) MARGIN_MM;
            double effectiveWidthPx = effectiveWidth / INCH_IN_MM * DPI;
            double effectiveHeightPx = effectiveHeight / INCH_IN_MM * DPI;
            int imgWidth = pdImage.getWidth();
            int imgHeight = pdImage.getHeight();
            double scaleX = effectiveWidthPx / imgWidth;
            double scaleY = effectiveHeightPx / imgHeight;
            double scale = Math.min(scaleX, scaleY);
            int newImgWidth = (int) (imgWidth * scale);
            int newImgHeight = (int) (imgHeight * scale);
            int marginPx = (int) (MARGIN_MM / INCH_IN_MM * DPI);
            int y = a4HeightPx - newImgHeight - marginPx;

            // PDF x,y coordinate starts from bottom left.
            contents.drawImage(pdImage, marginPx, y, newImgWidth, newImgHeight);
            doc.addPage(page);
        }
    }

    private boolean isJpeg(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension);
    }

    private void prepOutputDirectory(Path outDir) throws IOException {
        if (Files.exists(outDir)) {
            File[] files = Objects.requireNonNullElse(outDir.toFile().listFiles(), new File[0]);
            for (File f : files) {
                Files.deleteIfExists(f.toPath());
            }
        }
        Files.createDirectories(outDir);
    }

    private int compareFileName(File f1, File f2) {
        if (f1 == f2) {
            return 0;
        }
        if (Objects.isNull(f1)) {
            return -1;
        }
        if (Objects.isNull(f2)) {
            return 1;
        }

        return f1.getName().compareTo(f2.getName());
    }

    private void logMemUsage(String tag) {
        long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        log.info("[{}] Mem used: {} MB", tag, memUsed / 1024 / 1024);
    }
}
