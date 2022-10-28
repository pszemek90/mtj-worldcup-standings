package com.pszemek.mtjworldcupstandings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MtjWorldcupStandingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtjWorldcupStandingsApplication.class, args);
    }

}
