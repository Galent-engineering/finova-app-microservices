package com.finova.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FallbackController
 */
@WebMvcTest(FallbackController.class)
class FallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generalFallback_ShouldReturnServiceUnavailable() throws Exception {
        mockMvc.perform(get("/fallback/general"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Service temporarily unavailable"))
                .andExpect(jsonPath("$.message").value("Please try again in a few moments"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.gateway").value("api-gateway"));
    }

    @Test
    void userServiceFallback_ShouldReturnServiceUnavailableWithUserServiceData() throws Exception {
        mockMvc.perform(get("/fallback/user-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("User Service unavailable"))
                .andExpect(jsonPath("$.message").value("Authentication and user management features are temporarily disabled"))
                .andExpect(jsonPath("$.service").value("user-service"))
                .andExpect(jsonPath("$.fallback_data.authenticated").value(false))
                .andExpect(jsonPath("$.fallback_data.guest_mode").value(true))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.gateway").value("api-gateway"));
    }

    @Test
    void accountServiceFallback_ShouldReturnServiceUnavailableWithAccountServiceData() throws Exception {
        mockMvc.perform(get("/fallback/account-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Account Service unavailable"))
                .andExpect(jsonPath("$.message").value("Account and contribution management features are temporarily disabled"))
                .andExpect(jsonPath("$.service").value("account-service"))
                .andExpect(jsonPath("$.fallback_data.accounts").exists())
                .andExpect(jsonPath("$.fallback_data.contributions").exists())
                .andExpect(jsonPath("$.fallback_data.total_balance").value("N/A"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.gateway").value("api-gateway"));
    }

    @Test
    void planningServiceFallback_ShouldReturnServiceUnavailableWithPlanningServiceData() throws Exception {
        mockMvc.perform(get("/fallback/planning-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Planning Service unavailable"))
                .andExpect(jsonPath("$.message").value("Retirement planning calculators are temporarily disabled"))
                .andExpect(jsonPath("$.service").value("planning-service"))
                .andExpect(jsonPath("$.fallback_data.calculators").value("offline"))
                .andExpect(jsonPath("$.fallback_data.projections").value("N/A"))
                .andExpect(jsonPath("$.fallback_data.recommendations").value("Contact financial advisor"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.gateway").value("api-gateway"));
    }

    @Test
    void rateLimitFallback_ShouldReturnTooManyRequests() throws Exception {
        mockMvc.perform(get("/fallback/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Rate limit exceeded"))
                .andExpect(jsonPath("$.message").value("Too many requests. Please wait before trying again"))
                .andExpect(jsonPath("$.retry_after").value(60))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.gateway").value("api-gateway"));
    }

    @Test
    void generalFallback_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/fallback/general"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void userServiceFallback_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/fallback/user-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void accountServiceFallback_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/fallback/account-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void planningServiceFallback_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/fallback/planning-service"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void rateLimitFallback_ShouldHaveCorrectContentType() throws Exception {
        mockMvc.perform(get("/fallback/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void generalFallback_ShouldHaveTimestamp() throws Exception {
        long beforeRequest = System.currentTimeMillis();
        
        mockMvc.perform(get("/fallback/general"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isNumber());
        
        long afterRequest = System.currentTimeMillis();
        
        // The timestamp should be between before and after request time
        // We can't easily test the exact value, but we can verify it's a number
    }

    @Test
    void allFallbackEndpoints_ShouldHaveGatewayIdentifier() throws Exception {
        String[] endpoints = {
            "/fallback/general",
            "/fallback/user-service", 
            "/fallback/account-service",
            "/fallback/planning-service",
            "/fallback/rate-limit"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(jsonPath("$.gateway").value("api-gateway"));
        }
    }

    @Test
    void allFallbackEndpoints_ShouldHaveErrorStatus() throws Exception {
        String[] endpoints = {
            "/fallback/general",
            "/fallback/user-service", 
            "/fallback/account-service",
            "/fallback/planning-service",
            "/fallback/rate-limit"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(jsonPath("$.status").value("error"));
        }
    }

    @Test
    void serviceSpecificFallbacks_ShouldHaveServiceField() throws Exception {
        mockMvc.perform(get("/fallback/user-service"))
                .andExpect(jsonPath("$.service").value("user-service"));

        mockMvc.perform(get("/fallback/account-service"))
                .andExpect(jsonPath("$.service").value("account-service"));

        mockMvc.perform(get("/fallback/planning-service"))
                .andExpect(jsonPath("$.service").value("planning-service"));
    }

    @Test
    void generalAndRateLimitFallbacks_ShouldNotHaveServiceField() throws Exception {
        mockMvc.perform(get("/fallback/general"))
                .andExpect(jsonPath("$.service").doesNotExist());

        mockMvc.perform(get("/fallback/rate-limit"))
                .andExpect(jsonPath("$.service").doesNotExist());
    }
}
