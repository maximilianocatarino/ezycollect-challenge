package io.ezycollect;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class Main {
    public static void main(String[] args) {
        // Set the 'local' profile by default if none is specified, to ensure LocalStack config loads
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", "local");
        }
        SpringApplication.run(Main.class, args);
    }
}