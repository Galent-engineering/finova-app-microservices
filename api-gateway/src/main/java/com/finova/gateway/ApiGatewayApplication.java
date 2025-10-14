package com.finova.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway Application for Finova Retirement Microservices
 * 
 * This gateway serves as the single entry point for all client requests,
 * providing routing and load balancing.
 * 
 * Gateway fetches service registry but doesn't register itself to avoid
 * eurekaAutoServiceRegistration issues with reactive applications.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
