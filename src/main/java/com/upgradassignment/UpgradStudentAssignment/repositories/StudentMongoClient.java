package com.upgradassignment.UpgradStudentAssignment.repositories;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class StudentMongoClient {

    private static final String COLLECTION_NAME = "students";

    private static final String DATABASE_NAME = "upgrad_assignment";

    private static StudentMongoClient studentMongoClient = null;

    private static MongoCollection<Document> studentsCollection = null;

    private static BaseMongoClient baseMongoClient = BaseMongoClient.getInstance();

    private StudentMongoClient(MongoCollection<Document> studentsCollection) {
        StudentMongoClient.studentsCollection = studentsCollection;
    }

    public static StudentMongoClient getInstance() {
        if (studentMongoClient != null) {
            return studentMongoClient;
        }
        MongoCollection<Document> studentsCollection = BaseMongoClient.mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        studentMongoClient = new StudentMongoClient(studentsCollection);
        return studentMongoClient;
    }

    public MongoCollection<Document> getStudentsCollection() {
        return studentsCollection;
    }
}
