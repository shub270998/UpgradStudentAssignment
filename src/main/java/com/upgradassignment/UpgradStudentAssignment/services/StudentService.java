package com.upgradassignment.UpgradStudentAssignment.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.upgradassignment.UpgradStudentAssignment.models.SemesterStudent;
import com.upgradassignment.UpgradStudentAssignment.models.Student;
import com.upgradassignment.UpgradStudentAssignment.repositories.SemesterStudentMongoClient;
import com.upgradassignment.UpgradStudentAssignment.repositories.StudentMongoClient;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class StudentService {

    private final int MAXIMUM_MARKS = 1000;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private static final StudentMongoClient studentMongoClient = StudentMongoClient.getInstance();

    private static final SemesterStudentMongoClient semesterStudentMongoClient = SemesterStudentMongoClient.getInstance();

    private static final List<String> RANGES = new ArrayList<>(Arrays.asList("0-25","25-50","50-75","75-100"));


    public Document getStudent(ObjectId studentId) throws Exception {
        try {
            MongoCollection<Document> studentCollections = studentMongoClient.getStudentsCollection();
            MongoCollection<Document> semesterStudentCollections = semesterStudentMongoClient.getSemesterStudentsCollection();
            List<Document> studentsList = new ArrayList<>();
            Bson studentFilter = Filters.eq(Student.ID, studentId.toString());
            FindIterable<Document> studentsIterable = studentCollections.find(studentFilter);
            studentsIterable.into(studentsList);
            if (!studentsList.isEmpty()) {
                Document studentDocument = studentsList.getFirst();
                List<Document> semesterStudentList = new ArrayList<>();
                Bson semesterStudentFilter = Filters.eq(SemesterStudent.STUDENT_ID, studentId.toString());
                FindIterable<Document> semesterStudentIterable = semesterStudentCollections.find(semesterStudentFilter);
                semesterStudentIterable.into(semesterStudentList);
                List<Document> marks = semesterStudentList.stream().map(semesterStudent -> {
                    Integer totalMarksObtained = (Integer) semesterStudent.get(SemesterStudent.TOTAL_MARKS_OBTAINED);
                    float percentage = (float) totalMarksObtained / MAXIMUM_MARKS;
                    percentage *= 100;
                    percentage = Float.parseFloat(DECIMAL_FORMAT.format(percentage));
                    Document markEntity = new Document(){{
                        put("semester", semesterStudent.get(SemesterStudent.SEMESTER));
                        put("marks", totalMarksObtained);
                    }};
                    markEntity.put("percentage", percentage);
                    return markEntity;
                }).toList();
                studentDocument.put("marks",marks);
                return studentDocument;
            } else {
                throw new Exception("Invalid studentId");
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    public HashMap<Object,Object> getFilteredStudents(String studentId, Integer semester, Integer admissionYear, String branchCode, String course, Integer page, Integer pageSize) {
        try {
            HashMap<Object,Object> response = new HashMap<>();
            MongoCollection<Document> studentCollections = studentMongoClient.getStudentsCollection();
            List<Bson> filters = new ArrayList<>();
            Bson searchFilters = null;
            int pageValue = page != null ? page : 0;
            int pageSizeValue = pageSize != null ? pageSize : 10;
            if (studentId != null) {
                filters.add(Filters.eq(Student.ID, studentId));
            }
            if (semester != null) {
                filters.add(Filters.eq(Student.CURRENT_SEMESTER, semester));
            }
            if (admissionYear != null) {
                filters.add(Filters.eq(Student.ADMISSION_YEAR, admissionYear));
            }
            if (branchCode != null) {
                filters.add(Filters.eq(Student.BRANCH_CODE, branchCode));
            }
            if (course != null) {
                filters.add(Filters.eq(Student.COURSE, course));
            }
            searchFilters = !filters.isEmpty() ? Filters.and(filters) : Filters.empty();
            Bson sort = Sorts.ascending(Student.ID);
            long count = studentCollections.countDocuments(searchFilters);
            int skippedValue = pageValue * pageSizeValue;
            skippedValue = skippedValue >= count ? 0 : skippedValue;
            FindIterable<Document> studentsIterable = studentCollections.find(searchFilters).sort(sort).skip(skippedValue).limit(pageSizeValue);
            List<Document> studentsList = new ArrayList<>();
            studentsIterable.into(studentsList);
            response.put("total",count);
            response.put("data",studentsList);
            return response;
        } catch (Exception exception) {
            throw exception;
        }
    }

    public List<Document> getStudentsPerformance(Integer semester,String branchCode) {
        try {
            MongoCollection<Document> studentCollections = studentMongoClient.getStudentsCollection();
            MongoCollection<Document> semesterStudentCollections = semesterStudentMongoClient.getSemesterStudentsCollection();
            Bson searchFilters = Filters.eq(Student.BRANCH_CODE, branchCode);
            Bson projections = Projections.include(Student.ID);
            FindIterable<Document> studentsIterable = studentCollections.find(searchFilters).projection(projections);
            List<Document> studentsList = new ArrayList<>();
            studentsIterable.into(studentsList);
            List<Bson> aggregationStagesBsonList = new ArrayList<>();

            // Matching
            aggregationStagesBsonList.add(
                    new Document(){{
                        put("$match", new Document(){{
                            put("semester", semester);
                            put("studentId", new Document(){{
                                put("$in", studentsList.stream().map(student -> student.get(Student.ID)).toList());
                            }});
                        }});
                    }}
            );

            // Projecting the data  for response
            aggregationStagesBsonList.add(
                    new Document(){{
                        put("$project", new Document(){{
                            put("percentage", new Document(){{
                                put("$multiply", new ArrayList<Object>(Arrays.asList(new Document(){{
                                    put("$divide", new ArrayList<Object>(Arrays.asList("$totalMarksObtained", 1000)));
                                }},
                                 100
                                )));
                            }});
                        }});
                    }}
            );




            // One way to get the range wise response result is via grouping using the database
            // Second way is to calculate the range count programmatically using getRangesList() function which takes projected aggregated document in sorted list (line number 215)

            // Grouping the data
            aggregationStagesBsonList.add(
                new Document(){{
                    put("$group", new Document(){{
                        put("_id", new Document(){{
                            put("$cond", new ArrayList<>(
                               Arrays.asList(
                                   new Document() {{
                                         put("$and", new ArrayList<>(
                                             Arrays.asList(
                                                new Document("$lte", Arrays.asList("$percentage",100)),
                                                new Document("$gte", Arrays.asList("$percentage", 75))
                                             )
                                         ));
                                   }},
                                   "75-100",
                                   new Document(){{
                                       put("$cond", new ArrayList<>(
                                           Arrays.asList(
                                               new Document() {{
                                                   put("$and", new ArrayList<>(
                                                       Arrays.asList(
                                                           new Document("$lt", Arrays.asList("$percentage",75)),
                                                           new Document("$gte", Arrays.asList("$percentage", 50))
                                                       )
                                                   ));
                                               }},
                                               "50-75",
                                               new Document(){{
                                                   put("$cond", new ArrayList<>(
                                                       Arrays.asList(
                                                           new Document() {{
                                                               put("$and", new ArrayList<>(
                                                                   Arrays.asList(
                                                                       new Document("$lt", Arrays.asList("$percentage",50)),
                                                                       new Document("$gte", Arrays.asList("$percentage", 25))
                                                                   )
                                                               ));
                                                           }},
                                                           "25-50",
                                                           "0-25"
                                                       )
                                                   ));
                                               }}
                                           )
                                       ));
                                   }}
                               )
                            ));
                        }});
                        put("total", new Document(){{
                            put("$sum", 1);
                        }});
                    }});
                }}
            );

            List<Document> aggregateList = new ArrayList<>();
            List<Document> responseList = new ArrayList<>();
            AggregateIterable<Document> aggregatedDocuments = semesterStudentCollections.aggregate(aggregationStagesBsonList);
            aggregatedDocuments.into(aggregateList);
            // aggregateList = aggregateList.stream().sorted(Comparator.comparingDouble(document -> (double) document.get("percentage"))).toList();
            for (String range: RANGES) {
                Document responseDocument = new Document() {{
                    put("range", range);
                    put("total", 0);
                }};
                List<Document> aggregatedDocument = aggregateList.stream().filter(aggregateDocument -> aggregateDocument.get(SemesterStudent.ID_KEY).equals(range)).toList();
                if (!aggregatedDocument.isEmpty()) {
                    responseDocument.put("total", aggregatedDocument.getFirst().get("total"));
                }
                responseList.add(responseDocument);
            }

            return responseList;
        }catch (Exception exception) {
            throw exception;
        }
    }

    List<Document> getRangesList(List<Document> sortedDocuments) {
        List<Document> answer = new ArrayList<>();
        List<Integer> limits = new ArrayList<>(Arrays.asList(25,50,75,100));
        int i = 0; int prevLimit = -1;int currentLimit = 0;
        while (i < sortedDocuments.size() && currentLimit < limits.size()) {
            int count = 0;
            if (currentLimit != limits.size()-1) {
                while (i < sortedDocuments.size() && (double) sortedDocuments.get(i).get("percentage") < (double) limits.get(currentLimit)) {
                    i++;
                    count++;
                }
            } else {
                count = sortedDocuments.size() - i;
            }
            int prevRange = prevLimit == -1 ? 0 : limits.get(prevLimit);
            int nextRange = currentLimit;
            Document responseDocument = new Document() {{
                put("range", prevRange + "-" + limits.get(nextRange));
            }};
            responseDocument.put("total", count);
            answer.add(responseDocument);
            prevLimit = currentLimit;
            currentLimit++;
        }
        for (String range: RANGES) {
            Document responseDocument = new Document() {{
                put("range", range);
                put("total", 0);
            }};
            List<Document> aggregatedDocument = answer.stream().filter(aggregateDocument -> aggregateDocument.get("range").equals(range)).toList();
            if (aggregatedDocument.isEmpty()) {
                answer.add(responseDocument);
            }
        }
        return answer;
    }


}
