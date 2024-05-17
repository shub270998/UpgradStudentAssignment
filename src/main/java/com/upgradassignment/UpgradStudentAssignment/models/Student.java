package com.upgradassignment.UpgradStudentAssignment.models;

import java.util.ArrayList;
import java.util.List;

public class Student {

    public static final String ID = "_id";
    public static final String CODE = "code";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String BRANCH_CODE = "branchCode";
    public static final String COURSE = "course";
    public static final String ADMISSION_YEAR = "admissionYear";
    public static final String  CURRENT_SEMESTER = "currentSemester";

    public static enum BRANCH_CODE_ENUM {
        CSE,IT,ME,EE,EC,IC;

        public static List<String> getDisplayValues() {
            List<String> values = new ArrayList<>();
            for (BRANCH_CODE_ENUM branchCodeEnum : BRANCH_CODE_ENUM.values()) {
                values.add(branchCodeEnum.toString());
            }
            return values;
        }
    }

    public static enum COURSE_ENUM {
        BE,MTECH,BCA,MCA,MBA,BBA;

        public static List<String> getDisplayValues() {
            List<String> values = new ArrayList<>();
            for (COURSE_ENUM courseEnum : COURSE_ENUM.values()) {
                values.add(courseEnum.toString());
            }
            return values;
        }
    }
}
