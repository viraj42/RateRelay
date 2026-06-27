package com.raterelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RateRelayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateRelayApplication.class, args);
    }

}
