package com.finova.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Configuration class for API Gateway
 * Configures rate limiting, service discovery, and other gateway features
 */
@Configuration
public class GatewayConfig {

    /**
     * Primary key resolver for rate limiting based on client IP address
     */
    @Bean(name = "ipKeyResolver")
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> getClientIp(exchange);
    }
    
    /**
     * Alternative key resolver based on user ID (when authentication is implemented)
     */
    @Bean(name = "userKeyResolver")
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getRequest().getHeaders().getFirst("X-User-Id") != null
                ? Mono.just(exchange.getRequest().getHeaders().getFirst("X-User-Id"))
                : getClientIp(exchange);
    }
    
    /**
     * Extract client IP from request
     */
    private Mono<String> getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return Mono.just(xForwardedFor.split(",")[0].trim());
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return Mono.just(xRealIp);
        }
        
        return Mono.just(exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                : "unknown");
    }
}
