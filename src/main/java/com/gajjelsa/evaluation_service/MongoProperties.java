package com.gajjelsa.evaluation_service;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Validated
public class MongoProperties {
    @NotNull
    private String uri;

    @NotNull
    private String database;


    private Integer connectionTimeout;

    @NotNull
    private Boolean sslEnabled;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    public Boolean getSslEnabled() {
        return sslEnabled;
    }
    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }
}
