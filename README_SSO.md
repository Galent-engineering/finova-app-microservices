# SSO Implementation for Planning Service

This document describes the Single Sign-On (SSO) implementation added to the Finova Planning Service using OAuth 2.0 and JWT tokens.

## Overview

The Planning Service now supports SSO integration using Spring Security OAuth 2.0 Resource Server. The implementation is flexible and can work with various OAuth 2.0 providers like Keycloak, Auth0, Azure AD, and others.

## Features Added

### 1. Authentication & Authorization
- **OAuth 2.0 Resource Server**: Validates JWT tokens from OAuth providers
- **Role-Based Access Control**: Supports roles like `USER`, `ADMIN`, `FINANCIAL_ADVISOR`
- **Method-Level Security**: Uses `@PreAuthorize` annotations for fine-grained control
- **User Context**: Provides easy access to current user information throughout the application

### 2. Security Components

#### SecurityConfig (`src/main/java/com/finova/planning/security/SecurityConfig.java`)
- Configures Spring Security with OAuth 2.0 Resource Server
- Handles JWT token validation and authority extraction
- Supports conditional OAuth enabling (can be disabled for development)
- Configures CORS and security headers

#### UserContext (`src/main/java/com/finova/planning/security/UserContext.java`)
- Utility class for accessing current user information
- Provides methods to get user ID, username, email, roles, etc.
- Thread-safe access to authentication context

#### AuthenticationService (`src/main/java/com/finova/planning/service/AuthenticationService.java`)
- Service layer for authentication operations
- Validates user access to resources
- Manages user roles and authorities
- Provides token validation methods

### 3. DTOs and Models

#### UserDTO (`src/main/java/com/finova/planning/dto/UserDTO.java`)
- Represents user information extracted from JWT tokens
- Contains user ID, username, email, roles, authorities, tenant information
- Helper methods for role and authority checking

#### AuthenticationResponse (`src/main/java/com/finova/planning/dto/AuthenticationResponse.java`)
- Standard response format for authentication operations
- Includes user information, token details, and status messages

### 4. Controllers

#### AuthController (`src/main/java/com/finova/planning/controller/AuthController.java`)
- `/api/auth/me` - Get current user information
- `/api/auth/status` - Get authentication status
- `/api/auth/validate` - Validate JWT token
- `/api/auth/roles` - Get user roles and authorities
- `/api/auth/has-role/{role}` - Check specific role
- `/api/auth/has-authority/{authority}` - Check specific authority
- `/api/auth/validate-access/{userId}` - Validate access to user resources
- `/api/auth/tenant` - Get current tenant information
- `/api/auth/logout` - Logout current user
- `/api/auth/claims` - Get JWT claims (admin only)
- `/api/auth/config` - Get authentication configuration (admin only)

#### Updated PlanningController
- All endpoints now require authentication when OAuth is enabled
- Access control based on user ownership and roles
- Support for `ADMIN` and `FINANCIAL_ADVISOR` roles to access any user's data

## Configuration

### Development Mode (OAuth Disabled)
By default, OAuth is disabled for development. All endpoints are accessible without authentication.

```yaml
finova:
  security:
    oauth:
      enabled: false  # OAuth disabled for development
```

### Production Mode (OAuth Enabled)

#### 1. Enable OAuth
```yaml
finova:
  security:
    oauth:
      enabled: true  # Enable OAuth
```

#### 2. Configure OAuth Provider

**For Keycloak:**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/auth/realms/finova
          jwk-set-uri: http://localhost:8080/auth/realms/finova/protocol/openid-connect/certs
```

**For Auth0:**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-domain.auth0.com/
          jwk-set-uri: https://your-domain.auth0.com/.well-known/jwks.json
```

**For Azure AD:**
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://sts.windows.net/{tenant-id}/
          jwk-set-uri: https://login.microsoftonline.com/{tenant-id}/discovery/v2.0/keys
```

### CORS Configuration
Configure allowed origins for your frontend applications:

```yaml
finova:
  security:
    cors:
      allowed-origins:
        - http://localhost:3000  # React dev server
        - http://localhost:8000  # Your frontend
        - http://localhost:8080  # Keycloak
        - http://localhost:4200  # Angular dev server
        - https://*.finova.com   # Production domains
```

## JWT Token Structure

The service expects JWT tokens with the following claims:

### Standard Claims
- `sub` - Subject (user identifier)
- `email` - User email address
- `preferred_username` - Username
- `given_name` - First name
- `family_name` - Last name
- `name` - Full name

### Custom Claims
- `user_id` - Numeric user ID (optional)
- `roles` - Array of user roles
- `authorities` - Array of user authorities/permissions
- `tenant_id` - Tenant identifier for multi-tenancy

### Keycloak Specific Claims
- `realm_access.roles` - Realm-level roles from Keycloak

## Role-Based Access Control

### Supported Roles
- `USER` - Standard user role
- `ADMIN` - Administrator role (access to all resources)
- `FINANCIAL_ADVISOR` - Financial advisor role (access to client data)
- `SYSTEM_ADMIN` - System administrator (full system access)

### Access Rules
1. **User Data Access**: Users can only access their own data
2. **Admin Access**: `ADMIN` and `SYSTEM_ADMIN` can access any user's data
3. **Financial Advisor Access**: `FINANCIAL_ADVISOR` can access client data (with proper tenant context)

## API Endpoints

### Public Endpoints (No Authentication Required)
- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application info
- `GET /api/planning/health` - Planning service health
- `GET /api/auth/health` - Authentication service health
- `GET /h2-console/**` - H2 database console (development)

### Protected Endpoints (Authentication Required)
- `GET /api/planning/retirement-plan/{userId}` - Get retirement plan
- `POST /api/planning/retirement-plan/calculate` - Calculate retirement plan
- `GET /api/planning/scenarios/{userId}` - Get what-if scenarios
- `GET /api/planning/social-security/{userId}` - Get Social Security benefits
- `POST /api/planning/social-security/calculate` - Calculate SS benefits
- `GET /api/planning/investment-strategy/{userId}` - Get investment strategy
- `GET /api/planning/tools/{userId}` - Get planning tools
- `GET /api/planning/dashboard/{userId}` - Get planning dashboard
- All `/api/auth/**` endpoints (except health)

## Testing

### Test with OAuth Disabled (Development)
1. Start the application (OAuth is disabled by default)
2. All endpoints should be accessible without authentication
3. Test example: `curl http://localhost:8083/api/planning/health`

### Test with OAuth Enabled (Production)
1. Set up an OAuth 2.0 provider (Keycloak, Auth0, etc.)
2. Enable OAuth in configuration
3. Obtain a JWT token from your provider
4. Include the token in API requests: `Authorization: Bearer <jwt-token>`

### Testing Authentication Endpoints
```bash
# Get current user (requires authentication)
curl -H "Authorization: Bearer <jwt-token>" http://localhost:8083/api/auth/me

# Check authentication status
curl -H "Authorization: Bearer <jwt-token>" http://localhost:8083/api/auth/status

# Get user roles
curl -H "Authorization: Bearer <jwt-token>" http://localhost:8083/api/auth/roles

# Check specific role
curl -H "Authorization: Bearer <jwt-token>" http://localhost:8083/api/auth/has-role/ADMIN
```

## Troubleshooting

### Common Issues

1. **CORS Errors**: Add your frontend origin to `finova.security.cors.allowed-origins`
2. **JWT Validation Errors**: Check issuer-uri and jwk-set-uri configuration
3. **Access Denied**: Verify user has correct roles and user ID matches resource access
4. **OAuth Provider Connection**: Ensure OAuth provider is running and accessible

### Debug Logging
Enable debug logging for authentication issues:
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
    org.springframework.web.cors: DEBUG
    com.finova.planning: DEBUG
```

## Security Best Practices

1. **Use HTTPS in Production**: Always use HTTPS for production deployments
2. **Validate JWT Signatures**: Ensure proper JWT signature validation
3. **Short-lived Tokens**: Use short-lived access tokens with refresh tokens
4. **Principle of Least Privilege**: Grant minimal required permissions
5. **Audit Logging**: Log authentication and authorization events
6. **Regular Updates**: Keep OAuth libraries and dependencies updated

## Migration from Non-SSO

If migrating from a non-SSO version:

1. **Development**: No changes needed, OAuth is disabled by default
2. **Production**: 
   - Set up OAuth 2.0 provider
   - Update configuration with OAuth settings
   - Enable OAuth: `finova.security.oauth.enabled: true`
   - Update frontend to handle JWT tokens
   - Test thoroughly before deployment

## Future Enhancements

Potential improvements for the SSO implementation:

1. **Token Refresh**: Implement automatic token refresh
2. **Session Management**: Add session-based authentication fallback
3. **Multi-factor Authentication**: Support MFA integration
4. **Audit Trail**: Enhanced logging for security events
5. **Rate Limiting**: Add rate limiting for authentication endpoints
6. **Token Blacklisting**: Implement token revocation/blacklisting
7. **Custom Claims**: Support for custom business-specific claims

---

For additional support or questions about the SSO implementation, please refer to the Spring Security OAuth 2.0 documentation or contact the development team.
