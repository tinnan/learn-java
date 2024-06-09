package com.example.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class MongoConnectionConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoConnectionConfig.class);
    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;
    @Value("${spring.data.mongodb.dbname}")
    private String databaseName;
    @Value("${spring.data.mongodb.authSource:admin}")
    private String authDatabaseName;
    @Value("${mongo.maxidletime:600000}")
    private long maxIdleTime;
    @Value("${mongo.maxlifetime:3600000}")
    private long maxLifeTime;
    @Value("${mongo.minsize:1}")
    private int minSize;
    @Value("${mongo.maxsize:100}")
    private int maxSize;
    @Value("${mongo.connect.timeout:10000}")
    private int connectionTimeout;
    @Value("${mongo.read.timeout:15000}")
    private int readTimeout;
    @Value("${mongo.maintenanceFrequency:60000}")
    private int maintenanceFrequency;

    public MongoConnectionConfig() {
    }

    public MongoClientSettings getMongoSetting() {
        String uri = this.mongodbUri;
        logger.info("initial mongodb connection : {}", uri);
        logger.info("initial mongodb authSource: {}", this.authDatabaseName);
        logger.info("initial mongo maxidletime : {}", this.maxIdleTime);
        logger.info("initial mongo maxlifetime : {}", this.maxLifeTime);
        logger.info("initial mongo minsize : {}", this.minSize);
        logger.info("initial mongo maxsize : {}", this.maxSize);
        logger.info("initial mongo connect timeout : {}", this.connectionTimeout);
        logger.info("initial mongo read timeout : {}", this.readTimeout);
        logger.info("initial mongo maintenance frequency : {}", this.maintenanceFrequency);
        return MongoClientSettings.builder().applyToConnectionPoolSettings((pool) -> {
            pool.maxConnectionIdleTime(this.maxIdleTime, TimeUnit.MILLISECONDS)
                .maxConnectionLifeTime(this.maxLifeTime, TimeUnit.MILLISECONDS).minSize(this.minSize)
                .maxSize(this.maxSize);
        }).applyConnectionString(new ConnectionString(uri)).applyToSocketSettings((socket) -> {
            socket.readTimeout(this.readTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(this.connectionTimeout, TimeUnit.MILLISECONDS);
        }).credential(MongoCredential.createCredential(this.username,
            StringUtils.hasText(this.authDatabaseName) ? this.authDatabaseName : this.databaseName,
            this.password.toCharArray())).build();
    }
}
