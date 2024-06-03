package com.mongodb.tutorials.csfle.automatic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientBilling {
    private String cartType;
    private String cardNumber;
}
