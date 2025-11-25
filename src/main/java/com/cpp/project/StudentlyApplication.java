package com.cpp.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class StudentlyApplication {
    private static final Logger logger = LoggerFactory.getLogger(StudentlyApplication.class);

    public static void main(String[] args) {
        logger.info("Starting studently application...");
        SpringApplication.run(StudentlyApplication.class, args);
        logger.info("Studently application started successfully!");
    }
}
