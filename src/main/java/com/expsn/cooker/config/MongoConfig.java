package com.expsn.cooker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    
    @Bean
    public MongoClient mongoClient() {
        // Define a conexão manualmente
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        // Aqui você força o nome do banco 'cooker'
        return new MongoTemplate(mongoClient(), "cooker");
    }

    @Bean
    public boolean autoCreateIndices() {
        return true; 
    }
}
