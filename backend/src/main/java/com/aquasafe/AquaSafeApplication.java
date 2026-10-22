package com.aquasafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AquaSafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquaSafeApplication.class, args);
    }
}
