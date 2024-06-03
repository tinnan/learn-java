package com.mongodb.tutorials.csfle.automatic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRecord {
    private String ssn;
    private PatientBilling billing;
}
