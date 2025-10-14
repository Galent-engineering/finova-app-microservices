package com.finova.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Configuration Server Application for Finova Retirement Microservices
 * 
 * This server provides centralized configuration management for all microservices
 * in the Finova Retirement Planning ecosystem.
 * 
 * Config Server runs independently and does not register with Eureka.
 * Other services connect to it directly via http://localhost:8888
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
