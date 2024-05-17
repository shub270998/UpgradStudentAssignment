package com.upgradassignment.UpgradStudentAssignment.validation;


import com.upgradassignment.UpgradStudentAssignment.models.Student;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


@Service
public class StudentValidationService {


    public List<String> validateGetFilteredStudentRequest(String studentId, String branchCode, String course, Integer semester) {
        List<String> errors = new ArrayList<>();
        EnumSet<Student.BRANCH_CODE_ENUM> branchCodeEnumSet = EnumSet.allOf(Student.BRANCH_CODE_ENUM.class);
        EnumSet<Student.COURSE_ENUM> courseEnumSet = EnumSet.allOf(Student.COURSE_ENUM.class);
        if (studentId != null && (studentId.isEmpty() || !ObjectId.isValid(studentId))) {
          errors.add("Invalid Student Id");
        }
        if (branchCode != null) {
            try {
               Boolean valid = branchCodeEnumSet.contains(Student.BRANCH_CODE_ENUM.valueOf(branchCode));
            } catch (IllegalArgumentException e) {
                errors.add("Invalid Branch Code. Supported Values :- " + String.join(",",Student.BRANCH_CODE_ENUM.getDisplayValues()));
            }
        }
        if (course != null) {
            try {
                Boolean valid = courseEnumSet.contains(Student.COURSE_ENUM.valueOf(course));
            } catch (IllegalArgumentException e) {
                errors.add("Invalid Course Name. Supported Values :- " + String.join(",",Student.COURSE_ENUM.getDisplayValues()));
            }
        }
        if (semester != null) {
            if (semester < 1 || semester > 8) {
                errors.add("Semester value should be between 1 to 8");
            }
        }
        return errors;
    }

    public List<String> validateGetPerformanceStudentRequest(String branchCode,Integer semester) {
        List<String> errors = new ArrayList<>();
        EnumSet<Student.BRANCH_CODE_ENUM> branchCodeEnumSet = EnumSet.allOf(Student.BRANCH_CODE_ENUM.class);
        if (branchCode != null) {
            try {
                Boolean valid = branchCodeEnumSet.contains(Student.BRANCH_CODE_ENUM.valueOf(branchCode));
            } catch (IllegalArgumentException e) {
                errors.add("Invalid Branch Code. Supported Values :- " + String.join(",",Student.BRANCH_CODE_ENUM.getDisplayValues()));
            }
        }
        if (semester != null) {
            if (semester < 1 || semester > 8) {
                errors.add("Semester value should be between 1 to 8");
            }
        }
        return errors;
    }
}
