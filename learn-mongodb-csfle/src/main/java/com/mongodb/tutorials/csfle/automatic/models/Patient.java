package com.mongodb.tutorials.csfle.automatic.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Patient {
    private ObjectId id;
    private String patientName;
    private PatientRecord patientRecord;

    public Patient(String name, PatientRecord patientRecord) {
        this.patientName = name;
        this.patientRecord = patientRecord;
    }
}
