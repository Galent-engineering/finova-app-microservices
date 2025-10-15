# Finova Retirement - Frontend SSO Integration

This document describes the Single Sign-On (SSO) integration for the Finova Retirement frontend application using OAuth 2.0 and OIDC with Keycloak.

## 📋 Overview

The frontend has been enhanced with comprehensive SSO authentication capabilities that integrate seamlessly with the backend planning service's security implementation. Users must authenticate through Keycloak before accessing the retirement planning dashboard.

## 🏗️ Architecture

### Authentication Flow
1. User accesses the application
2. Authentication module checks for valid stored tokens
3. If no valid authentication, user sees login screen
4. User clicks "Login with SSO" → redirected to Keycloak
5. After successful Keycloak authentication → redirected back to callback.html
6. Callback page exchanges authorization code for tokens
7. Tokens stored securely, user info retrieved from backend
8. Main application loads with authenticated user context

### Files Added/Modified

#### New Authentication Files:
- **`auth.js`** - Complete SSO authentication module
- **`auth-styles.css`** - UI styles for authentication components
- **`callback.html`** - OAuth callback handler page
- **`README_SSO_Frontend.md`** - This documentation

#### Modified Files:
- **`index.html`** - Added auth script includes
- **`script.js`** - Integrated authentication with existing API calls

## 🔐 Authentication Features

### Core Functionality
- **OAuth 2.0 / OIDC** integration with Keycloak
- **JWT token** management with automatic refresh
- **Secure token storage** in localStorage
- **Role-based access control** (USER, ADMIN, FINANCIAL_ADVISOR, SYSTEM_ADMIN)
- **CSRF protection** with state parameter validation
- **Session timeout** handling
- **Graceful error handling** and user feedback

### User Experience
- **Seamless login/logout** workflow
- **Professional login screen** with branded UI
- **User dropdown menu** with profile information and logout
- **Responsive design** for mobile and desktop
- **Loading states** and error messages
- **Automatic token refresh** to maintain sessions

### Security Features
- **State parameter** validation to prevent CSRF attacks
- **Token expiry** checking and automatic refresh
- **Secure logout** with Keycloak session termination
- **Authorization header** injection for API calls
- **401 unauthorized** handling with automatic re-authentication

## ⚙️ Configuration

### SSO Configuration (auth.js)
```javascript
const SSO_CONFIG = {
    keycloakUrl: 'http://localhost:8080',      // Keycloak server URL
    realm: 'finova',                           // Keycloak realm name
    clientId: 'finova-frontend',               // OAuth client ID
    redirectUri: window.location.origin + '/callback.html',
    scope: 'openid profile email roles',      // Requested scopes
    // Token storage configuration
    storageKeys: { ... }
};
```

### Environment-Specific Settings
- **Development**: OAuth can be disabled in backend for testing
- **Production**: Full OAuth enforcement with Keycloak integration
- **Testing**: Mock authentication available for automated tests

## 🚀 Setup Instructions

### Prerequisites
1. **Keycloak Server** running on http://localhost:8080
2. **Backend Planning Service** with SSO enabled
3. **Realm Configuration** - "finova" realm in Keycloak
4. **Client Configuration** - "finova-frontend" client setup

### Keycloak Client Configuration
1. Create realm: `finova`
2. Create client: `finova-frontend`
3. Client settings:
   - Client ID: `finova-frontend`
   - Client Protocol: `openid-connect`
   - Access Type: `public`
   - Standard Flow Enabled: `ON`
   - Direct Access Grants: `OFF`
   - Valid Redirect URIs: `http://localhost:8000/callback.html`
   - Web Origins: `http://localhost:8000`

### Frontend Setup
1. Ensure all authentication files are in the frontend directory
2. Serve the frontend from a web server (port 8000 recommended)
3. Update `SSO_CONFIG` in `auth.js` if using different URLs
4. Test authentication flow

### Backend Integration
- Backend planning service should be configured for OAuth 2.0
- JWT token validation enabled
- Appropriate CORS settings for frontend origin
- User/role management integrated with Keycloak

## 🔧 API Integration

### Authenticated API Calls
All API calls now automatically include authentication:

```javascript
// Automatically adds Authorization header if user is authenticated
const data = await fetchFromAPI('/api/dashboard/123');

// Direct authenticated calls
const response = await authenticatedFetch(url, options);
```

### User Context
Access current user information throughout the application:

```javascript
const user = getCurrentUser();
if (hasRole('ADMIN')) {
    // Admin-specific functionality
}

if (canAccessUser(userId)) {
    // User-specific data access
}
```

### Role-Based Features
- **USER**: Access own retirement data
- **FINANCIAL_ADVISOR**: Access client data
- **ADMIN**: Manage system settings
- **SYSTEM_ADMIN**: Full system access

## 🎨 UI/UX Features

### Login Screen
- **Professional branding** with Finova logo
- **Single Sign-On button** with clear call-to-action
- **Feature highlights** (secure, personalized, role-based)
- **Responsive design** for all device sizes
- **Loading states** during authentication

### Authenticated Header
- **User greeting** with full name
- **User dropdown menu** with:
  - User profile information
  - Email address
  - Current roles
  - Logout button
- **Responsive behavior** on mobile devices

### Navigation Protection
- **Disabled navigation** when not authenticated
- **Visual feedback** (grayed out, no pointer events)
- **Automatic re-enable** after successful authentication

## 🚨 Error Handling

### Authentication Errors
- **Invalid credentials**: Clear error message with retry option
- **Network errors**: Helpful troubleshooting suggestions
- **Token expiry**: Automatic refresh or re-authentication
- **CSRF protection**: Security error with safe recovery

### API Call Errors
- **401 Unauthorized**: Automatic login redirect
- **403 Forbidden**: Access denied message with role information
- **Network timeouts**: Retry mechanisms with fallback data
- **Service unavailable**: Graceful degradation to cached data

## 🧪 Testing

### Manual Testing
1. **Unauthenticated Access**: Verify login screen appears
2. **Login Flow**: Test complete OAuth flow
3. **Dashboard Access**: Confirm data loads for authenticated user
4. **Role Permissions**: Test different user roles
5. **Logout Flow**: Verify complete session termination
6. **Token Refresh**: Test automatic token renewal
7. **Error Scenarios**: Test various error conditions

### Development Mode
- Backend OAuth can be disabled for development
- Use fallback sample data when services are unavailable
- Console logging for debugging authentication flow

## 📱 Browser Compatibility

### Supported Browsers
- **Chrome 90+**
- **Firefox 88+**
- **Safari 14+**
- **Edge 90+**

### Storage Requirements
- **LocalStorage** for token and user data
- **SessionStorage** for OAuth state management
- **Modern JavaScript** features (ES6+, async/await, fetch)

## 🔒 Security Best Practices

### Token Management
- Tokens stored in localStorage (consider httpOnly cookies for production)
- Automatic token cleanup on logout
- Token expiry validation before each API call
- Secure transmission over HTTPS (production)

### CSRF Protection
- State parameter validation in OAuth flow
- Random state generation for each login attempt
- Secure state storage in sessionStorage

### Content Security Policy
Consider implementing CSP headers:
```
script-src 'self' https://cdnjs.cloudflare.com;
connect-src 'self' http://localhost:8080 http://localhost:8083;
```

## 🚀 Deployment Considerations

### Production Checklist
- [ ] Update Keycloak URLs to production endpoints
- [ ] Configure HTTPS for all connections
- [ ] Set appropriate CORS policies
- [ ] Implement proper CSP headers
- [ ] Test all authentication flows
- [ ] Verify role-based access controls
- [ ] Monitor authentication metrics

### Environment Variables
Consider externalizing configuration:
- `KEYCLOAK_URL`
- `KEYCLOAK_REALM`
- `KEYCLOAK_CLIENT_ID`
- `API_BASE_URL`

## 🐛 Troubleshooting

### Common Issues

#### "Authentication Required" Loops
- Check Keycloak client configuration
- Verify redirect URLs match exactly
- Ensure backend accepts the JWT tokens

#### CORS Errors
- Check Keycloak CORS settings
- Verify backend CORS configuration
- Ensure all origins are properly configured

#### Token Validation Failures
- Check token format and claims
- Verify audience and issuer settings
- Ensure clock synchronization

#### Network Connection Issues
- Verify all service URLs are accessible
- Check firewall and proxy settings
- Test with browser developer tools

### Debug Information
Enable console logging by setting:
```javascript
localStorage.setItem('auth_debug', 'true');
```

## 🔄 Future Enhancements

### Planned Features
- **Remember me** functionality with refresh token persistence
- **Multi-factor authentication** support
- **Social login** providers (Google, Microsoft)
- **Progressive Web App** capabilities with offline support
- **Advanced role management** with fine-grained permissions

### Performance Optimizations
- **Token caching** strategies
- **Lazy loading** of authentication components
- **Background token refresh** without user interaction
- **Service worker** integration for offline capabilities

## 📞 Support

For authentication-related issues:
1. Check browser console for detailed error messages
2. Verify Keycloak server status and configuration
3. Test backend API endpoints independently
4. Review network traffic in browser dev tools
5. Consult backend authentication logs

---

## 📚 Related Documentation
- Backend SSO Implementation: `README_SSO.md`
- API Documentation: See planning service endpoints
- Keycloak Configuration: Official Keycloak documentation
- OAuth 2.0/OIDC Specification: RFC 6749, RFC 7517

---

**Last Updated**: November 2024
**Version**: 1.0.0
**Author**: AI Assistant
