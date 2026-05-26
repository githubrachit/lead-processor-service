package com.leadplatform.processor;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class LeadProcessorApplication {

    @PostConstruct
    public void init() {
        // Force the entire application context to use your local timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }
    public static void main(String[] args) {
        SpringApplication.run(LeadProcessorApplication.class, args);
    }
}
