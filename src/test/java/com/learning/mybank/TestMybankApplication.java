package com.learning.mybank;

import org.springframework.boot.SpringApplication;

public class TestMybankApplication {

    public static void main(String[] args) {
        SpringApplication.from(MybankApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
