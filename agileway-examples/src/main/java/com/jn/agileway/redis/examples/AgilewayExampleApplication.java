package com.jn.agileway.redis.examples;

import com.jn.agileway.springboot.web.rest.EnableGlobalRestHandlers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGlobalRestHandlers
public class AgilewayExampleApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(AgilewayExampleApplication.class, args);
        Thread.sleep(1000000000);
    }
}