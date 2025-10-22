package com.finova.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller for handling service unavailability
 * Provides specific fallback responses for each microservice
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/general")
    public Mono<ResponseEntity<Map<String, Object>>> generalFallback() {
        Map<String, Object> response = createBaseResponse(
            "Service temporarily unavailable", 
            "Please try again in a few moments"
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        Map<String, Object> response = createBaseResponse(
            "User Service unavailable", 
            "Authentication and user management features are temporarily disabled"
        );
        response.put("service", "user-service");
        response.put("fallback_data", Map.of(
            "authenticated", false,
            "guest_mode", true
        ));
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    @GetMapping("/account-service")
    public Mono<ResponseEntity<Map<String, Object>>> accountServiceFallback() {
        Map<String, Object> response = createBaseResponse(
            "Account Service unavailable", 
            "Account and contribution management features are temporarily disabled"
        );
        response.put("service", "account-service");
        response.put("fallback_data", Map.of(
            "accounts", Map.of(),
            "contributions", Map.of(),
            "total_balance", "N/A"
        ));
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    @GetMapping("/planning-service")
    public Mono<ResponseEntity<Map<String, Object>>> planningServiceFallback() {
        Map<String, Object> response = createBaseResponse(
            "Planning Service unavailable", 
            "Retirement planning calculators are temporarily disabled"
        );
        response.put("service", "planning-service");
        response.put("fallback_data", Map.of(
            "calculators", "offline",
            "projections", "N/A",
            "recommendations", "Contact financial advisor"
        ));
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    @GetMapping("/rate-limit")
    public Mono<ResponseEntity<Map<String, Object>>> rateLimitFallback() {
        Map<String, Object> response = createBaseResponse(
            "Rate limit exceeded", 
            "Too many requests. Please wait before trying again"
        );
        response.put("retry_after", 60); // seconds
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response));
    }
    
    private Map<String, Object> createBaseResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        response.put("gateway", "api-gateway");
        return response;
    }
}
