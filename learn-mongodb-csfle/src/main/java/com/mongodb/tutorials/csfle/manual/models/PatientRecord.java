package com.mongodb.tutorials.csfle.manual.models;

import java.util.Base64;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonBinary;

@Data
@NoArgsConstructor
public class PatientRecord {
    private BsonBinary ssn;
    private BsonBinary billing;

    public PatientRecord(BsonBinary ssn, BsonBinary billing) {
        this.ssn = ssn;
        this.billing = billing;
    }

    @Override
    public String toString() {
        return "PatientRecord{" +
            "ssn=" + bsobBinToString(ssn) +
            ", billing=" + bsobBinToString(billing) +
            '}';
    }

    private String bsobBinToString(BsonBinary src) {
        if (src == null) {
            return "{type=, data=}";
        }
        return "{type=" + src.getType() + ", data=" + Base64.getEncoder().encodeToString(src.getData()) + "}";
    }
}
