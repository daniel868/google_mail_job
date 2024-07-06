package org.example.google_mail_job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GoogleMailJobApplication {

    private static final Logger logger = LogManager.getLogger(GoogleMailJobApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(GoogleMailJobApplication.class, args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> {

        };
    }
}
