package com.gajjelsa.evaluation_service.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "com.gajjelsa.evaluation_service.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.connection-timeout:30000}")
    private Integer connectionTimeout;

    @Value("${spring.data.mongodb.ssl.enabled:true}")
    private Boolean sslEnabled;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        try {
            ConnectionString connectionString = new ConnectionString(mongoUri);

            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToSslSettings(builder ->
                            builder.enabled(sslEnabled))
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS))
                    .applyToConnectionPoolSettings(builder ->
                            builder.maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS)
                                    .maxSize(50))
                    .build();

            return MongoClients.create(mongoClientSettings);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MongoDB client: " + e.getMessage(), e);
        }
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder
                .applyToSocketSettings(socket ->
                        socket.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS))
                .applyToConnectionPoolSettings(pool ->
                        pool.maxSize(50)
                                .maxWaitTime(10000, TimeUnit.MILLISECONDS));
    }
}
