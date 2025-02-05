package com.example.poc.csv;

import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvExample {

    @SneakyThrows
    public static void main(String[] args) {
        String csvFile = "input.csv"; // Place your file in src/main/resources
        try (CSVReader reader = new CSVReader(new InputStreamReader(
            CsvExample.class.getClassLoader().getResourceAsStream(csvFile)))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                // Process the line
                log.info("{}", (Object) line);
            }
        }
    }
}