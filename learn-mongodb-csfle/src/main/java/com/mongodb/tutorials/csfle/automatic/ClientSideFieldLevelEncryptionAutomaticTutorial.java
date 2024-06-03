package com.mongodb.tutorials.csfle.automatic;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.mongodb.tutorials.csfle.automatic.models.Patient;
import com.mongodb.tutorials.csfle.automatic.models.PatientBilling;
import com.mongodb.tutorials.csfle.automatic.models.PatientRecord;
import com.mongodb.tutorials.csfle.util.EncryptionHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

@Slf4j
public class ClientSideFieldLevelEncryptionAutomaticTutorial {
    private static final String KMS_PROVIDER_NAME = "local";
    private static final String ALGO_DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
    private static final String ALGO_RANDOMIZED = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";
    private static final String KEY_VAULT_DATABASE_NAME = "encryption";
    private static final String KEY_VAULT_COLLECTION_NAME = "__keyVault";
    private static final String KEY_VAULT_NAMESPACE = KEY_VAULT_DATABASE_NAME + "." + KEY_VAULT_COLLECTION_NAME;
    private static final String ENCRYPTED_DATABASE_NAME = "medicalRecords";
    private static final String ENCRYPTED_COLLECTION_NAME = "patients";

    public static void main(String[] args) throws Exception {
        String uri = EncryptionHelpers.getEnv("MONGODB_URI");

        // Requires for mapping BsonDocument to POJO.
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        Map<String, Map<String, Object>> kmsProviderCredentials = EncryptionHelpers.getKmsProviderCredentials(
            KMS_PROVIDER_NAME);

        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
            .keyVaultMongoClientSettings(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build())
            .keyVaultNamespace(KEY_VAULT_NAMESPACE)
            .kmsProviders(kmsProviderCredentials)
            .build();
        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);

        // !!! Everytime clientEncryption.createDataKey is called,
        // a new record of data encryption key is created in "encryption.__keyVault".
        // !!! If master key is lost we won't be able to decrypt the data.
        BsonBinary dataKey = createKey(clientEncryption, "data-key");

        // Create encrypted schema setting.
        Map<String, BsonDocument> encryptionSchema = createJsonSchema(dataKey);
        AutoEncryptionSettings autoEncryptionOptions = EncryptionHelpers.getAutoEncryptionOptions(KEY_VAULT_NAMESPACE,
            kmsProviderCredentials, encryptionSchema);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .autoEncryptionSettings(autoEncryptionOptions)
            .build();

        try (MongoClient client = MongoClients.create(clientSettings)) {
            // Drop collections.
            client.getDatabase(ENCRYPTED_DATABASE_NAME).getCollection(ENCRYPTED_COLLECTION_NAME).drop();

            // The example use 2 data encryption key to encrypt each field.
            // Actually, you can choose to use same data encryption key to encrypt every field.
            MongoDatabase database = client.getDatabase(ENCRYPTED_DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Patient> collection = database.getCollection(ENCRYPTED_COLLECTION_NAME, Patient.class);

//            deterministicVsRandomize(collection);
            encryptDecrypt(collection);
//            sortBySsn(collection);
        }
    }

    private static void sortBySsn(MongoCollection<Patient> collection) {
        log.info("***** Sort by SSN *****");

        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Patient document1 = createDocument("Jon Doe 1", "B001", cardType, cardNumber);
        collection.insertOne(document1);

        Patient document2 = createDocument("Jon Doe 2", "Z001", cardType, cardNumber);
        collection.insertOne(document2);

        Patient document3 = createDocument("Jon Doe 3", "A001", cardType, cardNumber);
        collection.insertOne(document3);

        try (MongoCursor<Patient> sorted = collection.find().sort(Sorts.ascending("patientRecord.ssn")).iterator()) {
            while (sorted.hasNext()) {
                Patient doc = sorted.next();
                PatientRecord patientRecord = doc.getPatientRecord();
                log.info("Sorted: {}", patientRecord.getSsn());
            }
        }
        log.info("* !!! Sort by encrypted field cannot give correct result.");
    }

    private static void encryptDecrypt(MongoCollection<Patient> collection) {
        log.info("***** Encrypt / Decrypt *****");

        final String patientName = "Jon Doe";
        final String ssn = "987-65-4320";
        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Patient patient = createDocument(patientName, ssn, cardType, cardNumber);
        collection.insertOne(patient);

        // Can search with plain text value on non-object encrypted field.
        Patient result = collection.find(eq("patientRecord.ssn", ssn)).first();

        // Field "cardType" is inside encrypted field "billing". Cannot search on this field.
        // Error: Invalid operation on path 'patientRecord.billing.cardType' which contains an encrypted path prefix.
//        Patient result = collection.find(eq("patientRecord.billing.cardType", cardType)).first();
        if (result == null) {
            log.error("Not found data.");
            return;
        }
        log.info("Encrypted Document: {}", result);
        PatientRecord patientRecord = result.getPatientRecord();

        log.info("Decrypted SSN: {}", patientRecord.getSsn());
        log.info("Decrypted Billing: {}", patientRecord.getBilling());
    }

    private static void deterministicVsRandomize(MongoCollection<Patient> collection) {
        // Insert same data for 2 records.
        // - patientRecord.ssn field should store same binary result.
        // - patientRecord.billing field should store different binary result.
        log.info("***** Deterministic vs Randomized encryption *****");
        log.info("* Insert same data for 2 records");
        log.info("* - encrypted patientRecord.ssn field should store same binary result");
        log.info("* - encrypted patientRecord.billing field should store different binary result");

        final String patientName = "Jon Doe";
        final String ssn = "987-65-4320";
        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";
        // Insert 1
        Patient patient1 = createDocument(patientName, ssn, cardType, cardNumber);
        collection.insertOne(patient1);

        // Insert 2
        Patient patient2 = createDocument(patientName, ssn, cardType, cardNumber);
        collection.insertOne(patient2);

        // Cannot check from query result in program.
        // Export and verify from MongoDB Compass instead.
    }

    private static BsonBinary createKey(ClientEncryption clientEncryption, String keyAltName) {
        BsonDocument dataKeyDoc = clientEncryption.getKeyByAltName(keyAltName);
        if (dataKeyDoc != null) {
            return dataKeyDoc.getBinary("_id");
        }
        return clientEncryption.createDataKey(KMS_PROVIDER_NAME, new DataKeyOptions().keyAltNames(List.of(keyAltName)));
    }

    private static Patient createDocument(String patientName, String ssn, String cardType, String cardNumber) {

        return new Patient(patientName, new PatientRecord(ssn, new PatientBilling(cardType, cardNumber)));
    }

    private static Map<String, BsonDocument> createJsonSchema(BsonBinary dataKey) {
        Document jsonSchema = new Document()
            .append("bsonType", "object")
            .append("encryptMetadata", new Document()
                .append("keyId", new ArrayList<>((Arrays.asList(
                    new Document().append("$binary", new Document()
                        .append("base64", Base64.getEncoder().encodeToString(dataKey.getData()))
                        .append("subType", "04")
                    ))))
                )
            )
            .append("properties", new Document()
                .append("patientRecord", new Document()
                    .append("bsonType", "object")
                    .append("properties", new Document()
                        .append("ssn", new Document()
                            .append("encrypt", new Document()
                                .append("bsonType", "string")
                                .append("algorithm", ALGO_DETERMINISTIC)))
                        .append("billing", new Document()
                            .append("encrypt", new Document()
                                .append("bsonType", "object")
                                .append("algorithm", ALGO_RANDOMIZED))))));
        Map<String, BsonDocument> schemaMap = new HashMap<>();
        schemaMap.put("medicalRecords.patients", BsonDocument.parse(jsonSchema.toJson()));
        return schemaMap;
    }
}
