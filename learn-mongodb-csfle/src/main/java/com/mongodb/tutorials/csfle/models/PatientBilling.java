package com.mongodb.tutorials.csfle.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientBilling {
    private String cardType;
    private String cardNumber;

    public PatientBilling(String cardType, String cardNumber) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }
}
