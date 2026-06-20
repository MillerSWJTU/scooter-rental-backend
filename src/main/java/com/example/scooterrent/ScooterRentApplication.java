package com.example.scooterrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.scooterrent.entity")
@EnableJpaRepositories("com.example.scooterrent.repository")
@ComponentScan(basePackages = {"com.example.scooterrent"})
public class ScooterRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScooterRentApplication.class, args);
    }
}
