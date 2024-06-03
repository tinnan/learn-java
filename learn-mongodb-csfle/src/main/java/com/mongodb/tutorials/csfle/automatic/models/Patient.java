package com.mongodb.tutorials.csfle.automatic.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Patient {
    private ObjectId id;
    private String patientName;
    private String dekAltName;
    private PatientRecord patientRecord;

    public Patient(String name, String dekAltName, PatientRecord patientRecord) {
        this.patientName = name;
        this.dekAltName = dekAltName;
        this.patientRecord = patientRecord;
    }
}
