package com.juegodefinitivo.autobook.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.juegodefinitivo.autobook")
public class AutoBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoBookApplication.class, args);
    }
}
