package com.mongodb.tutorials.csfle;

import static com.mongodb.client.model.Filters.eq;

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
import com.mongodb.tutorials.csfle.util.EncryptionHelpers;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;

@Slf4j
public class ClientSideFieldLevelEncryptionTutorial {
    private static final String KMS_PROVIDER_NAME = "local";
    private static final String ALGO_DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";
    private static final String ALGO_RANDOMIZED = "AEAD_AES_256_CBC_HMAC_SHA_512-Random";

    public static void main(String[] args) throws Exception {
        String uri = EncryptionHelpers.getEnv("MONGODB_URI");

        String keyVaultDatabaseName = "encryption";
        String keyVaultCollectionName = "__keyVault";
        String keyVaultNamespace = keyVaultDatabaseName + "." + keyVaultCollectionName;
        String encryptedDatabaseName = "medicalRecords";
        String encryptedCollectionName = "patients";

        Map<String, Map<String, Object>> kmsProviderCredentials = EncryptionHelpers.getKmsProviderCredentials(
            KMS_PROVIDER_NAME);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .build();

        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
            .keyVaultMongoClientSettings(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build())
            .keyVaultNamespace(keyVaultNamespace)
            .kmsProviders(kmsProviderCredentials)
            .build();

        try (
            MongoClient client = MongoClients.create(clientSettings);
            ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings)
        ) {
            // Drop collections.
            client.getDatabase(encryptedDatabaseName).getCollection(encryptedCollectionName).drop();

            final String ssnKeyName = "ssn-data-key";
            final String billingKeyName = "billing-data-key";

            // !!! Everytime clientEncryption.createDataKey is called,
            // a new record of data encryption key is created in "encryption.__keyVault".
            // !!! If master key is lost we won't be able to decrypt the data.
            BsonBinary ssnDataKey = createKey(clientEncryption, ssnKeyName);
            BsonBinary billingDataKey = createKey(clientEncryption, billingKeyName);
            // The example use 2 data encryption key to encrypt each field.
            // Actually, you can choose to use same data encryption key to encrypt every field.
            MongoDatabase database = client.getDatabase(encryptedDatabaseName);
            MongoCollection<Document> collection = database.getCollection(encryptedCollectionName);

//            deterministicVsRandomize(clientEncryption, collection, ssnDataKey, billingDataKey);
//            encryptDecrypt(clientEncryption, collection, ssnDataKey, billingDataKey);
            sortBySsn(clientEncryption, collection, ssnDataKey, billingDataKey);
        }
    }

    private static void sortBySsn(ClientEncryption clientEncryption,
        MongoCollection<Document> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {

        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Document document1 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 1", "B001", cardType,
            cardNumber);
        insert(collection, document1);

        Document document2 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 2", "Z001", cardType,
            cardNumber);
        insert(collection, document2);

        Document document3 = createDocument(clientEncryption, ssnDataKey, billingDataKey, "Jon Doe 3", "A001", cardType,
            cardNumber);
        insert(collection, document3);

        // !!! Sort by encrypted field cannot give correct result.
        try (MongoCursor<Document> sorted = collection.find().sort(Sorts.ascending("patientRecord.ssn")).iterator()) {
            while (sorted.hasNext()) {
                Document doc = sorted.next();
                Document patientRecord = doc.get("patientRecord", Document.class);
                Binary ssnField = patientRecord.get("ssn", Binary.class);
                BsonValue decrypted = clientEncryption.decrypt(new BsonBinary(ssnField.getType(), ssnField.getData()));
                log.info("Sorted Decrypted: {}", decrypted);
            }
        }
    }

    private static void encryptDecrypt(ClientEncryption clientEncryption,
        MongoCollection<Document> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {

        final String patientName = "Jon Doe";
        final String ssn = "987-65-4320";
        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";

        Document document = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        insert(collection, document);

        BsonBinary encryptedSsn = clientEncryption.encrypt(new BsonString(ssn),
            new EncryptOptions(ALGO_DETERMINISTIC).keyId(ssnDataKey));
        Document result = collection.find(eq("patientRecord.ssn", encryptedSsn)).first();
        if (result == null) {
            log.error("Not found data.");
            return;
        }
        log.info("Encrypted Document: {}", result.toJson());
        Document patientRecord = result.get("patientRecord", Document.class);

        Binary ssnField = patientRecord.get("ssn", Binary.class);
        patientRecord.replace("ssn", clientEncryption.decrypt(new BsonBinary(ssnField.getType(), ssnField.getData())));

        Binary billingField = patientRecord.get("billing", Binary.class);
        patientRecord.replace("billing", clientEncryption.decrypt(new BsonBinary(billingField.getType(), billingField.getData())));
        log.info("Encrypted Document: {}", result.toJson());
    }

    private static BsonBinary createKey(ClientEncryption clientEncryption, String keyAltName) {
        BsonDocument dataKeyDoc = clientEncryption.getKeyByAltName(keyAltName);
        if (dataKeyDoc != null) {
            return dataKeyDoc.getBinary("_id");
        }
        return clientEncryption.createDataKey(KMS_PROVIDER_NAME, new DataKeyOptions().keyAltNames(List.of(keyAltName)));
    }

    private static void deterministicVsRandomize(ClientEncryption clientEncryption,
        MongoCollection<Document> collection, BsonBinary ssnDataKey, BsonBinary billingDataKey) {
        // Insert same data for 2 records.
        // - patientRecord.ssn field should store same binary result.
        // - patientRecord.billing field should store different binary result.

        final String patientName = "Jon Doe";
        final String ssn = "987-65-4320";
        final String cardType = "Visa";
        final String cardNumber = "4111111111111111";
        // Insert 1
        Document document1 = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        insert(collection, document1);

        // Insert 2 we should
        Document document2 = createDocument(clientEncryption, ssnDataKey, billingDataKey, patientName, ssn, cardType,
            cardNumber);
        insert(collection, document2);

        try (MongoCursor<Document> sorted = collection.find().iterator()) {
            while (sorted.hasNext()) {
                Document doc = sorted.next();
                Document patientRecord = doc.get("patientRecord", Document.class);
                Binary ssnField = patientRecord.get("ssn", Binary.class);
                BsonValue decrypted = clientEncryption.decrypt(new BsonBinary(ssnField.getType(), ssnField.getData()));
                log.info("Sorted Decrypted: {}", decrypted);
            }
        }
    }

    private static Document createDocument(ClientEncryption clientEncryption, BsonBinary ssnDataKey, BsonBinary billingDataKey,
        String patientName, String ssn, String cardType, String cardNumber) {

        BsonBinary encryptedSsn = clientEncryption.encrypt(new BsonString(ssn),
            new EncryptOptions(ALGO_DETERMINISTIC).keyId(ssnDataKey));
        BsonBinary encryptedPatientBilling = clientEncryption.encrypt(
            new BsonDocument("cardType", new BsonString(cardType))
                .append("cardNumber", new BsonString(cardNumber)),
            new EncryptOptions(ALGO_RANDOMIZED).keyId(billingDataKey));

        return new Document("patientName", new BsonString(patientName))
            .append("patientRecord", new Document("ssn", encryptedSsn)
                .append("billing", encryptedPatientBilling));
    }

    private static void insert(MongoCollection<Document> collection, Document document) {
        collection.insertOne(document);
    }
}
