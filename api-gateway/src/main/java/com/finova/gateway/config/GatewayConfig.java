package com.finova.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for API Gateway
 * Gateway acts as a client to discover services but doesn't register itself
 */
@Configuration
public class GatewayConfig {
    // Gateway configuration - fetches registry but doesn't register
}
