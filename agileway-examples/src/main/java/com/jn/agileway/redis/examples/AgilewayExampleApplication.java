package com.jn.agileway.redis.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgilewayExampleApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(AgilewayExampleApplication.class, args);
        Thread.sleep(1000000000);
    }
}