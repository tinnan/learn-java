package com.mongodb.tutorials.qe;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
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
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateEncryptedCollectionParams;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.mongodb.tutorials.qe.models.Patient;
import com.mongodb.tutorials.qe.models.PatientBilling;
import com.mongodb.tutorials.qe.models.PatientRecord;
import com.mongodb.tutorials.qe.util.QueryableEncryptionHelpers;
import java.util.Arrays;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class QueryableEncryptionTutorial {
    public static void main(String[] args) throws Exception {
        // start-setup-application-variables
        // KMS provider name should be one of the following: "aws", "gcp", "azure", "kmip" or "local"
        String kmsProviderName = "local";

        String uri = QueryableEncryptionHelpers.getEnv("MONGODB_URI"); // Your connection URI

        // todo: at which point that database "encryption" and collection "__keyVault" are created?
        String keyVaultDatabaseName = "encryption";
        String keyVaultCollectionName = "__keyVault";
        String keyVaultNamespace = keyVaultDatabaseName + "." + keyVaultCollectionName;
        String encryptedDatabaseName = "medicalRecords";
        String encryptedCollectionName = "patients";
        // end-setup-application-variables

        // start-setup-application-pojo
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        // end-setup-application-pojo

        Map<String, Map<String, Object>> kmsProviderCredentials = QueryableEncryptionHelpers.getKmsProviderCredentials(kmsProviderName);
        BsonDocument customerMasterKeyCredentials = QueryableEncryptionHelpers.getCustomerMasterKeyCredentials(kmsProviderName);

        AutoEncryptionSettings autoEncryptionSettings = QueryableEncryptionHelpers.getAutoEncryptionOptions(keyVaultNamespace, kmsProviderCredentials);
        // start-create-client
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .autoEncryptionSettings(autoEncryptionSettings)
                .build();

        try (MongoClient encryptedClient = MongoClients.create(clientSettings)) {
            // end-create-client
            // todo: __keyVault and patients collections are dropped here.
            //  Maybe, this is to reset database every time the tutorial app runs?
            encryptedClient.getDatabase(keyVaultDatabaseName).getCollection(keyVaultCollectionName).drop();
            encryptedClient.getDatabase(encryptedDatabaseName).getCollection(encryptedCollectionName).drop();

            // start-encrypted-fields-map
            // todo: ssn and billing are encrypted fields.
            BsonDocument encryptedFieldsMap = new BsonDocument().append("fields",
                    new BsonArray(Arrays.asList(
                            new BsonDocument()
                                    .append("keyId", new BsonNull())
                                    .append("path", new BsonString("patientRecord.ssn"))
                                    .append("bsonType", new BsonString("string"))
                                    .append("queries", new BsonDocument()
                                            .append("queryType", new BsonString("equality"))),
                            new BsonDocument()
                                    .append("keyId", new BsonNull())
                                    .append("path", new BsonString("patientRecord.billing"))
                                    .append("bsonType", new BsonString("object")))));
            // end-encrypted-fields-map

            // start-client-encryption
            ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                    .keyVaultMongoClientSettings(MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(uri))
                            .build())
                    .keyVaultNamespace(keyVaultNamespace)
                    .kmsProviders(kmsProviderCredentials)
                    .build();
            ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
            // end-client-encryption

            // start-create-encrypted-collection
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().encryptedFields(encryptedFieldsMap);

            CreateEncryptedCollectionParams encryptedCollectionParams = new CreateEncryptedCollectionParams(kmsProviderName);
            encryptedCollectionParams.masterKey(customerMasterKeyCredentials);

            try {
                // todo: medicalRecords collection created here.
                clientEncryption.createEncryptedCollection(
                        encryptedClient.getDatabase(encryptedDatabaseName),
                        encryptedCollectionName,
                        createCollectionOptions,
                        encryptedCollectionParams);
            }
            // end-create-encrypted-collection
            catch (Exception e) {
                throw new Exception("Unable to create encrypted collection due to the following error: " + e.getMessage());
            }

            // start-insert-document
            // todo: what is codec registry?
            MongoDatabase encryptedDb = encryptedClient.getDatabase(encryptedDatabaseName).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Patient> collection = encryptedDb.getCollection(encryptedCollectionName, Patient.class);

            insertPatient(collection,
                new Patient("Jon Doe",
                    new PatientRecord("987-65-4320",
                        new PatientBilling("Visa", "4111111111111111"))));
            insertPatient(collection,
                new Patient("Jane Doe",
                    new PatientRecord("772-65-6727",
                        new PatientBilling("MasterCard", "4111111111199101"))));

            // start-find-document
            System.out.println("Find by SSN");
            Patient findBySsnResult = findBySsn(collection, "987-65-4320");
            System.out.println(findBySsnResult);

            System.out.println("Find by Patient name pattern");
            MongoCursor<Patient> findByNamePatternResult = findByPatientNamePattern(collection, "^.*Doe");
            while (findByNamePatternResult.hasNext()) {
                Patient patient = findByNamePatternResult.next();
                System.out.println(patient);
            }

            // $regex operator is not supported on encrypted field.
//            System.out.println("Find by SSN pattern");
//            MongoCursor<Patient> findBySsnPatternResult = findBySsnPattern(collection, "^[0-9]{3}-65-[0-9]{4}");
//            while (findBySsnPatternResult.hasNext()) {
//                Patient patient = findBySsnPatternResult.next();
//                System.out.println(patient);
//            }
            // end-find-document
        }
    }

    private static Patient findBySsn(MongoCollection<Patient> collection, String ssn) {
        return collection.find(
                new BsonDocument()
                    .append("patientRecord.ssn", new BsonString(ssn)))
            .first();
    }

    private static MongoCursor<Patient> findByPatientNamePattern(MongoCollection<Patient> collection, String pattern) {
        return collection.find(
                new BsonDocument()
                    .append("patientName", new BsonRegularExpression(pattern)))
            .iterator();
    }

    private static MongoCursor<Patient> findBySsnPattern(MongoCollection<Patient> collection, String pattern) {
        return collection.find(
                new BsonDocument()
                    .append("patientRecord.ssn", new BsonRegularExpression(pattern)))
            .iterator();
    }

    private static void insertPatient(MongoCollection<Patient> collection, Patient patientDocument) {
        InsertOneResult result = collection.insertOne(patientDocument);
        // end-insert-document
        if (result.wasAcknowledged()) {
            System.out.println("Successfully inserted the patient document.");
        }
    }
}
