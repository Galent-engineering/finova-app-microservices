package com.finova.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GatewayConfig
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gateway.routes[0].id=test-route",
    "spring.cloud.gateway.routes[0].uri=http://localhost:8080",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/test/**"
})
class GatewayConfigTest {

    @Autowired
    private GatewayConfig gatewayConfig;

    private KeyResolver ipKeyResolver;
    private KeyResolver userKeyResolver;

    @BeforeEach
    void setUp() {
        ipKeyResolver = gatewayConfig.ipKeyResolver();
        userKeyResolver = gatewayConfig.userKeyResolver();
    }

    @Test
    void ipKeyResolver_WithXForwardedForHeader_ShouldReturnFirstIp() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Forwarded-For", "192.168.1.1, 10.0.0.1, 172.16.0.1")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.1", ip);
    }

    @Test
    void ipKeyResolver_WithXRealIpHeader_ShouldReturnRealIp() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Real-IP", "203.0.113.1")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("203.0.113.1", ip);
    }

    @Test
    void ipKeyResolver_WithRemoteAddress_ShouldReturnRemoteAddress() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .remoteAddress(new InetSocketAddress("192.168.1.100", 8080))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.100", ip);
    }

    @Test
    void ipKeyResolver_WithNoHeaders_ShouldReturnUnknown() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("unknown", ip);
    }

    @Test
    void userKeyResolver_WithXUserIdHeader_ShouldReturnUserId() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "12345")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String userId = result.block();
        assertEquals("12345", userId);
    }

    @Test
    void userKeyResolver_WithoutXUserIdHeader_ShouldFallbackToIpResolver() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Forwarded-For", "192.168.1.50")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.50", ip);
    }

    @Test
    void userKeyResolver_WithNullXUserIdHeader_ShouldFallbackToIpResolver() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", (String) null)
                .remoteAddress(new InetSocketAddress("192.168.1.75", 8080))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.75", ip);
    }

    @Test
    void userKeyResolver_WithEmptyXUserIdHeader_ShouldFallbackToIpResolver() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "")
                .remoteAddress(new InetSocketAddress("192.168.1.80", 8080))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.80", ip);
    }

    @Test
    void ipKeyResolver_WithMultipleCommasInXForwardedFor_ShouldReturnFirstIp() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Forwarded-For", "203.0.113.1, 203.0.113.2, 203.0.113.3")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("203.0.113.1", ip);
    }

    @Test
    void ipKeyResolver_WithSpacesInXForwardedFor_ShouldTrimSpaces() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Forwarded-For", "  192.168.1.10  ,  10.0.0.1  ")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = ipKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String ip = result.block();
        assertEquals("192.168.1.10", ip);
    }

    @Test
    void userKeyResolver_WithValidUserId_ShouldReturnUserId() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "user123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String userId = result.block();
        assertEquals("user123", userId);
    }

    @Test
    void userKeyResolver_WithNumericUserId_ShouldReturnUserId() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "98765")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        Mono<String> result = userKeyResolver.resolve(exchange);

        // Then
        assertNotNull(result);
        String userId = result.block();
        assertEquals("98765", userId);
    }
}