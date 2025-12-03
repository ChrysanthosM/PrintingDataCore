package org.masouras.config;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(3, TimeUnit.SECONDS))
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(3, TimeUnit.SECONDS))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        String dbName = new ConnectionString(connectionString).getDatabase();
        Preconditions.checkNotNull(dbName);
        return new MongoTemplate(mongoClient, dbName);
    }
}
