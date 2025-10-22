# Frontend Authentication & API Gateway Fixes

## Issues Identified

1. **Wrong API Gateway Port**: The frontend was trying to connect to port 9080 (Keycloak) instead of port 8080 (API Gateway)
2. **Services not properly showing login screen**

## Fixes Applied

### 1. Fixed API Gateway URL in `script.js`
- **Before**: `const API_GATEWAY_URL = 'http://localhost:9080';`
- **After**: `const API_GATEWAY_URL = 'http://localhost:8080';`

### 2. Fixed Port References in `index.html`
- Updated all references from port 9080 to 8080 for API Gateway
- Kept port 9080 only for Keycloak SSO (which is correct)

### 3. Authentication Flow
The authentication flow is now:
1. Page loads → `initializeApp()` is called
2. `initializeAuth()` checks for valid token
3. If no token → `updateUIForUnauthenticatedUser()` shows login screen
4. If token exists → validates with backend → shows dashboard

## Testing Steps

### Step 1: Clear Browser Storage
Open browser console and run:
```javascript
localStorage.clear();
sessionStorage.clear();
location.reload();
```

### Step 2: Reload the Page
You should now see the **Login Screen** with "Login with SSO" button.

### Step 3: Test Login Flow
1. Click "Login with SSO"
2. Should redirect to Keycloak at `http://localhost:9080`
3. Login with: `john.doe` / `password123`
4. Should redirect back and show the dashboard

### Step 4: Verify API Calls
Open Network tab in browser DevTools and verify:
- All API calls go to `http://localhost:8080` (API Gateway)
- NOT to `http://localhost:9080` (which is Keycloak)
- Bearer token is included in Authorization header

## Expected Behavior

### When NOT Logged In:
- Shows login prompt screen
- Navigation is disabled (grayed out)
- No API calls to services
- Header shows "Login with SSO" button

### When Logged In:
- Shows dashboard with real data
- Navigation is enabled
- API calls include Bearer token
- Header shows user info and logout option

## Port Reference Guide

| Service | Correct Port | Purpose |
|---------|-------------|---------|
| Frontend | 8000 | Web UI (Python HTTP server) |
| **API Gateway** | **8080** | **All API calls go here** |
| User Service | 8081 | Direct access (optional) |
| Account Service | 8082 | Direct access (optional) |
| Planning Service | 8083 | Direct access (optional) |
| Payment Service | 8084 | Direct access (optional) |
| Analytics Service | 8085 | Direct access (optional) |
| Eureka Server | 8761 | Service Discovery |
| Config Server | 8888 | Configuration |
| **Keycloak (SSO)** | **9080** | **Authentication only** |

## Troubleshooting

### Issue: Still seeing "Loading..." on dashboard
**Solution**: Clear browser storage and reload:
```javascript
localStorage.clear();
sessionStorage.clear();
location.reload();
```

### Issue: Login button not appearing
**Solution**: 
1. Check browser console for errors
2. Verify `auth.js` is loaded before `script.js` in `index.html` (it is - line 568-569)
3. Check if `updateUIForUnauthenticatedUser()` function is being called

### Issue: API calls fail with 404
**Solution**: Verify API Gateway is running on port 8080:
```bash
curl http://localhost:8080/actuator/health
```

### Issue: Authentication fails
**Solution**: Verify Keycloak is running on port 9080:
```bash
curl http://localhost:9080/realms/finova/.well-known/openid-configuration
```

## Files Modified
- `/frontend/script.js` - Fixed API Gateway URL
- `/frontend/index.html` - Fixed port references

## Files NOT Modified (Working Correctly)
- `/frontend/auth.js` - SSO authentication (port 9080 for Keycloak is correct)
- `/frontend/payments.js` - Direct service access
- `/frontend/analytics.js` - Direct service access

