package com.upgradassignment.UpgradStudentAssignment.repositories;


import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class SemesterStudentMongoClient {

    private static final String COLLECTION_NAME = "semesterStudents";

    private static final String DATABASE_NAME = "upgrad_assignment";

    private static SemesterStudentMongoClient semesterStudentMongoClient = null;

    private static MongoCollection<Document> semesterStudentsCollection = null;

    private static BaseMongoClient baseMongoClient = BaseMongoClient.getInstance();

    private SemesterStudentMongoClient(MongoCollection<Document> semesterStudentsCollection) {
        SemesterStudentMongoClient.semesterStudentsCollection = semesterStudentsCollection;
    }

    public static SemesterStudentMongoClient getInstance() {
        if (semesterStudentMongoClient != null) {
            return semesterStudentMongoClient;
        }
        MongoCollection<Document> semesterStudentsCollection = BaseMongoClient.mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        semesterStudentMongoClient = new SemesterStudentMongoClient(semesterStudentsCollection);
        return semesterStudentMongoClient;
    }

    public MongoCollection<Document> getSemesterStudentsCollection() {
        return semesterStudentsCollection;
    }
}
