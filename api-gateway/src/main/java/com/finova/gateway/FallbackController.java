package com.finova.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller for handling service unavailability
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<Map<String, String>> fallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Service is temporarily unavailable. Please try again later.");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return Mono.just(response);
    }
}
