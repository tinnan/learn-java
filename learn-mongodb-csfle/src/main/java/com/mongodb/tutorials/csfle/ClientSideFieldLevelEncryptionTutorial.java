package com.mongodb.tutorials.csfle;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.mongodb.tutorials.csfle.models.Patient;
import com.mongodb.tutorials.csfle.models.PatientRecord;
import com.mongodb.tutorials.csfle.util.EncryptionHelpers;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

@Slf4j
public class ClientSideFieldLevelEncryptionTutorial {
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

        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .build();

        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
            .keyVaultMongoClientSettings(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build())
            .keyVaultNamespace(KEY_VAULT_NAMESPACE)
            .kmsProviders(kmsProviderCredentials)
            .build();

        try (
            MongoClient client = MongoClients.create(clientSettings);
            ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings)
        ) {
            // Drop collections.
            client.getDatabase(ENCRYPTED_DATABASE_NAME).getCollection(ENCRYPTED_COLLECTION_NAME).drop();

            final String ssnKeyName = "ssn-data-key";
            final String billingKeyName = "billing-data-key";

            // !!! Everytime clientEncryption.createDataKey is called,
            // a new record of data encryption key is created in "encryption.__keyVault".
            // !!! If master key is lost we won't be able to decrypt the data.
            BsonBinary ssnDataKey = createKey(clientEncryption, ssnKeyName);
            BsonBinary billingDataKey = createKey(clientEncryption, billingKeyName);
            // The example use 2 data encryption key to encrypt each field.
            // Actually, you can choose to use same data encryption key to encrypt every field.
            MongoDatabase database = client.getDatabase(ENCRYPTED_DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Patient> collection = database.getCollection(ENCRYPTED_COLLECTION_NAME, Patient.class);

//            deterministicVsRandomize(clientEncryption, collection, ssnDataKey, billingDataKey);
//            encryptDecrypt(clientEncryption, collection, ssnDataKey, billingDataKey);
            sortBySsn(clientEncryption, collection, ssnDataKey, billingDataKey);
        }
    }

    private static void sortBySsn(ClientEncryption clientEncryption,
        MongoCollection<Patient> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {
        log.info("***** Sort by SSN *****");

        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Patient document1 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 1", "B001", cardType,
            cardNumber);
        collection.insertOne(document1);

        Patient document2 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 2", "Z001", cardType,
            cardNumber);
        collection.insertOne(document2);

        Patient document3 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 3", "A001", cardType,
            cardNumber);
        collection.insertOne(document3);

        try (MongoCursor<Patient> sorted = collection.find().sort(Sorts.descending("patientRecord.ssn")).iterator()) {
            while (sorted.hasNext()) {
                Patient doc = sorted.next();
                PatientRecord patientRecord = doc.getPatientRecord();
                BsonValue decrypted = clientEncryption.decrypt(patientRecord.getSsn());
                log.info("Sorted: {}", decrypted);
            }
        }
        log.info("* !!! Sort by encrypted field cannot give correct result.");
    }

    private static void encryptDecrypt(ClientEncryption clientEncryption,
        MongoCollection<Patient> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {
        log.info("***** Encrypt / Decrypt *****");

        final String patientName = "Jon Doe";
        final String ssn = "987-65-4320";
        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Patient patient = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        collection.insertOne(patient);

        BsonBinary encryptedSsn = clientEncryption.encrypt(new BsonString(ssn),
            new EncryptOptions(ALGO_DETERMINISTIC).keyId(ssnDataKey));
        Patient result = collection.find(eq("patientRecord.ssn", encryptedSsn)).first();
        if (result == null) {
            log.error("Not found data.");
            return;
        }
        log.info("Encrypted Document: {}", result);
        PatientRecord patientRecord = result.getPatientRecord();

        log.info("Decrypted SSN: {}", clientEncryption.decrypt(patientRecord.getSsn()));
        log.info("Decrypted Billing: {}", clientEncryption.decrypt(patientRecord.getBilling()));
    }

    private static void deterministicVsRandomize(ClientEncryption clientEncryption,
        MongoCollection<Patient> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {
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
        Patient patient1 = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        collection.insertOne(patient1);

        // Insert 2
        Patient patient2 = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        collection.insertOne(patient2);

        try (MongoCursor<Patient> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                log.info("{}", cursor.next());
            }
        }
    }

    private static BsonBinary createKey(ClientEncryption clientEncryption, String keyAltName) {
        BsonDocument dataKeyDoc = clientEncryption.getKeyByAltName(keyAltName);
        if (dataKeyDoc != null) {
            return dataKeyDoc.getBinary("_id");
        }
        return clientEncryption.createDataKey(KMS_PROVIDER_NAME, new DataKeyOptions().keyAltNames(List.of(keyAltName)));
    }

    private static Patient createDocument(ClientEncryption clientEncryption, BsonBinary ssnDataKey, BsonBinary billingDataKey,
        String patientName, String ssn, String cardType, String cardNumber) {

        BsonBinary encryptedSsn = clientEncryption.encrypt(new BsonString(ssn),
            new EncryptOptions(ALGO_DETERMINISTIC).keyId(ssnDataKey));
        BsonBinary encryptedPatientBilling = clientEncryption.encrypt(
            new BsonDocument("cardType", new BsonString(cardType))
                .append("cardNumber", new BsonString(cardNumber)),
            new EncryptOptions(ALGO_RANDOMIZED).keyId(billingDataKey));

        return new Patient(patientName, new PatientRecord(encryptedSsn, encryptedPatientBilling));
    }
}
