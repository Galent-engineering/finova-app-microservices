package com.finova.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Security configuration for OAuth 2.0 Resource Server - User Service
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
    private String jwkSetUri;

    @Value("${finova.security.oauth.enabled:false}")
    private boolean oauthEnabled;

    @Value("${finova.security.cors.allowed-origins:http://localhost:3000,http://localhost:8000,http://localhost:8080}")
    private String allowedOriginsStr;
    
    private String[] getAllowedOrigins() {
        return allowedOriginsStr.split(",");
    }

    @Value("${finova.security.public-paths:/actuator/**,/api/users/health,/error}")
    private String publicPathsStr;
    
    private String[] getPublicPaths() {
        return publicPathsStr.split(",");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                // Disable CSRF for stateless API
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configure authorization based on OAuth enabled status
                .authorizeHttpRequests(authz -> {
                    if (oauthEnabled && isOAuthConfigured()) {
                        // OAuth enabled - require authentication
                        authz
                            // Public endpoints using AntPathRequestMatcher
                            .requestMatchers(Arrays.stream(getPublicPaths()).map(AntPathRequestMatcher::new).toArray(AntPathRequestMatcher[]::new)).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/actuator/health"), new AntPathRequestMatcher("/actuator/info")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/api/auth/health"), new AntPathRequestMatcher("/api/users/health")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // For development
                            
                            // User endpoints require authentication
                            .requestMatchers(new AntPathRequestMatcher("/api/users/**")).authenticated()
                            .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).authenticated()
                            
                            // Any other request requires authentication
                            .anyRequest().authenticated();
                    } else {
                        // OAuth disabled - allow all for development
                        authz.anyRequest().permitAll();
                    }
                });
                
        // Configure OAuth 2.0 Resource Server conditionally
        if (oauthEnabled && isOAuthConfigured()) {
            try {
                httpSecurity.oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
            } catch (Exception e) {
                // If OAuth configuration fails, log warning and continue without OAuth
                System.out.println("Warning: OAuth configuration failed, continuing without authentication: " + e.getMessage());
            }
        }
        
        return httpSecurity
                // Configure security headers
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // For H2 console
                        .contentTypeOptions(contentType -> contentType.disable())
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)))
                .build();
    }

    /**
     * Check if OAuth is properly configured
     */
    private boolean isOAuthConfigured() {
        return (issuerUri != null && !issuerUri.trim().isEmpty()) || 
               (jwkSetUri != null && !jwkSetUri.trim().isEmpty());
    }

    /**
     * JWT Decoder configuration
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        if (issuerUri != null && !issuerUri.isEmpty()) {
            // Use issuer URI for automatic configuration
            return JwtDecoders.fromIssuerLocation(issuerUri);
        } else if (jwkSetUri != null && !jwkSetUri.isEmpty()) {
            // Use JWK Set URI
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } else {
            // Fallback for when OAuth is disabled - create a dummy decoder
            return NimbusJwtDecoder.withJwkSetUri("http://localhost:9999/dummy").build();
        }
    }

    /**
     * JWT Authentication Converter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        
        // Set authorities converter to handle both roles and authorities
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Get authorities from 'authorities' claim
            JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
            authoritiesConverter.setAuthorityPrefix("");
            Collection<? extends org.springframework.security.core.GrantedAuthority> authorities = 
                    authoritiesConverter.convert(jwt);

            // Get roles from 'roles' claim and add ROLE_ prefix
            Collection<String> roles = jwt.getClaimAsStringList("roles");
            Collection<SimpleGrantedAuthority> roleAuthorities = roles != null ?
                    roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList()) :
                    java.util.Collections.emptyList();

            // Get realm roles if present (for Keycloak compatibility)
            Collection<String> realmRoles = getRealmRoles(jwt);
            Collection<SimpleGrantedAuthority> realmAuthorities = realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());

            // Combine all authorities
            return Stream.of(
                    authorities != null ? authorities.stream() : Stream.<org.springframework.security.core.GrantedAuthority>empty(),
                    roleAuthorities.stream(),
                    realmAuthorities.stream()
            )
            .flatMap(stream -> stream)
            .collect(Collectors.toSet());
        });
        
        // Set principal name from preferred claim
        converter.setPrincipalClaimName("preferred_username");
        
        return converter;
    }

    /**
     * Extract realm roles from JWT (for Keycloak compatibility)
     */
    private Collection<String> getRealmRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof java.util.Map) {
            Object roles = ((java.util.Map<?, ?>) realmAccess).get("roles");
            if (roles instanceof Collection) {
                return ((Collection<?>) roles).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList());
            }
        }
        return java.util.Collections.emptyList();
    }

    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOriginPatterns(Arrays.asList(getAllowedOrigins()));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Set allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Tenant-ID",
                "X-Client-ID"
        ));
        
        // Set exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "X-Total-Count"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Set max age
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
