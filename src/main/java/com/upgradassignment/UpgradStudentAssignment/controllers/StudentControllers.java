package com.upgradassignment.UpgradStudentAssignment.controllers;



import com.upgradassignment.UpgradStudentAssignment.services.StudentService;
import com.upgradassignment.UpgradStudentAssignment.validation.StudentValidationService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentControllers {


    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentValidationService studentValidationService;


    @GetMapping("")
    @ResponseBody
    public ResponseEntity<Object> getStudentById(@RequestParam String id) {
        if (id.isEmpty() || !ObjectId.isValid(id)) {
            return new ResponseEntity<>("Invalid studentId", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            Document studentDocument = studentService.getStudent(new ObjectId(id));
            return new ResponseEntity<>(studentDocument, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Object> getFilteredStudents(@RequestParam(required = false) String studentId,
                                                      @RequestParam(required = false) Integer semester,
                                                      @RequestParam(required = false) Integer admissionYear,
                                                      @RequestParam(required = false) String branchCode,
                                                      @RequestParam(required = false) String course,
                                                      @RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer pageSize) {

        try {
            List<String> errors = studentValidationService.validateGetFilteredStudentRequest(studentId,branchCode,course,semester);
            if (!errors.isEmpty()) {
                return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            HashMap<Object,Object> response = studentService.getFilteredStudents(studentId, semester, admissionYear, branchCode, course, page, pageSize);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/performance")
    @ResponseBody
    public ResponseEntity<Object> getStudentsPerformance(@RequestParam Integer semester,
                                                      @RequestParam String branchCode) {
        try {
            List<String> errors = studentValidationService.validateGetPerformanceStudentRequest(branchCode,semester);
            if (!errors.isEmpty()) {
                return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            List<Document> aggregatedResponseDocument = studentService.getStudentsPerformance(semester,branchCode);
            return new ResponseEntity<>(aggregatedResponseDocument, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
