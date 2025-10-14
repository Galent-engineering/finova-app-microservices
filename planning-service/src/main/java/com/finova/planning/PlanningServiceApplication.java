package com.finova.planning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Planning Service Application for Finova Retirement Microservices
 * 
 * This service handles retirement planning calculations, projections,
 * and financial planning tools.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PlanningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanningServiceApplication.class, args);
    }
}
