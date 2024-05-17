package com.upgradassignment.UpgradStudentAssignment;

import com.upgradassignment.UpgradStudentAssignment.services.StudentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class UpgradStudentAssignmentApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(UpgradStudentAssignmentApplication.class, args);
	}

}
