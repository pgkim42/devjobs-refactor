package com.example.devjobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.example.devjobs")
public class DevJobsApplication {

    Dotenv dotenv = Dotenv.load();
        dotenv.entries().

    forEach(entry ->System.setProperty(entry.getKey(),entry.getValue()));

    public static void main(String[] args) {
        SpringApplication.run(DevJobsApplication.class, args);
    }

}
