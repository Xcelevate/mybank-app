package com.learning.mybank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class MybankApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MybankApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // non‑web
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Type 'quit' to exit:");
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine(); // waits for user input
                if ("quit".equalsIgnoreCase(line)) {
                    break;
                }
                System.out.println("You typed: " + line);
            }
        }
    }

}
