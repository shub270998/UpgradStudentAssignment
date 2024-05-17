package com.upgradassignment.UpgradStudentAssignment.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;

public class BaseMongoClient {

    public static MongoClient mongoClient = null;

    private static BaseMongoClient baseMongoClient = null;

    private static final String URI = "mongodb://localhost:27017";

    static MongoClient getClient() {
        return new MongoClientImpl(MongoClientSettings.builder().applyConnectionString(new ConnectionString((URI))).build(), MongoDriverInformation.builder().build());
    }

    private BaseMongoClient(MongoClient mongoClient) {
        BaseMongoClient.mongoClient = mongoClient;
    }

    public static synchronized BaseMongoClient getInstance() {
        if (baseMongoClient != null) {
            return baseMongoClient;
        }
        baseMongoClient = new BaseMongoClient(getClient());
        return baseMongoClient;
    }
}
