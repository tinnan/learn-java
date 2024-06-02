package com.mongodb.tutorials.csfle.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientRecord {
    private String ssn;
    private PatientBilling billing;

    public PatientRecord(String ssn, PatientBilling billing) {
        this.ssn = ssn;
        this.billing = billing;
    }
}
