# Frontend SSO Integration Guide

This guide shows how to integrate your frontend (running on port 8000) with the Keycloak SSO and Planning Service.

## 🎯 Overview

Your frontend will:
1. **Redirect users to Keycloak** for authentication
2. **Receive JWT tokens** after successful login
3. **Include JWT tokens** in API calls to Planning Service
4. **Handle token refresh** and logout

## 🔧 Frontend Integration Options

### Option 1: Simple HTML/JavaScript (Quick Demo)

Create this HTML file to test SSO integration:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finova Planning - SSO Demo</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 800px; margin: 0 auto; }
        .user-info, .api-response { 
            background: #f5f5f5; padding: 20px; margin: 20px 0; 
            border-radius: 5px; white-space: pre-wrap; 
        }
        .button { 
            background: #007bff; color: white; padding: 10px 20px; 
            border: none; border-radius: 5px; cursor: pointer; margin: 5px;
        }
        .button:hover { background: #0056b3; }
        .error { color: red; }
        .success { color: green; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🏦 Finova Planning Service - SSO Demo</h1>
        
        <div id="loginSection">
            <h2>Login Required</h2>
            <p>Please login to access the Planning Service.</p>
            <button class="button" onclick="login()">Login with SSO</button>
        </div>

        <div id="userSection" style="display: none;">
            <h2>Welcome!</h2>
            <div id="userInfo" class="user-info"></div>
            
            <h3>Test API Endpoints</h3>
            <button class="button" onclick="testUserInfo()">Get User Info</button>
            <button class="button" onclick="testRetirementPlan()">Get Retirement Plan</button>
            <button class="button" onclick="testRoles()">Check Roles</button>
            <button class="button" onclick="testAdminEndpoint()">Test Admin Endpoint</button>
            <button class="button" onclick="logout()">Logout</button>
            
            <h3>API Response</h3>
            <div id="apiResponse" class="api-response"></div>
        </div>
    </div>

    <script>
        // Configuration
        const config = {
            keycloakUrl: 'http://localhost:8080',
            realm: 'finova',
            clientId: 'finova-frontend',
            redirectUri: 'http://localhost:8000/callback.html',
            planningServiceUrl: 'http://localhost:8083'
        };

        // Check if user is already logged in
        window.onload = function() {
            const token = localStorage.getItem('access_token');
            if (token) {
                // Verify token is still valid
                fetch(`${config.planningServiceUrl}/api/auth/me`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Token expired');
                })
                .then(data => {
                    showUserSection(data.user);
                })
                .catch(() => {
                    localStorage.removeItem('access_token');
                    showLoginSection();
                });
            } else {
                // Check if we're returning from Keycloak with auth code
                handleCallback();
            }
        };

        function login() {
            const authUrl = `${config.keycloakUrl}/realms/${config.realm}/protocol/openid-connect/auth?` +
                `client_id=${config.clientId}&` +
                `redirect_uri=${encodeURIComponent(config.redirectUri)}&` +
                `response_type=code&` +
                `scope=openid profile email`;
            
            window.location.href = authUrl;
        }

        function handleCallback() {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            
            if (code) {
                // Exchange authorization code for tokens
                exchangeCodeForToken(code);
            }
        }

        function exchangeCodeForToken(code) {
            const tokenUrl = `${config.keycloakUrl}/realms/${config.realm}/protocol/openid-connect/token`;
            
            const params = new URLSearchParams({
                grant_type: 'authorization_code',
                client_id: config.clientId,
                code: code,
                redirect_uri: config.redirectUri
            });

            fetch(tokenUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params
            })
            .then(response => response.json())
            .then(data => {
                if (data.access_token) {
                    localStorage.setItem('access_token', data.access_token);
                    localStorage.setItem('refresh_token', data.refresh_token);
                    
                    // Clean up URL
                    window.history.replaceState({}, document.title, window.location.pathname);
                    
                    // Get user info and show user section
                    getUserInfo();
                } else {
                    showError('Failed to get access token');
                }
            })
            .catch(error => {
                showError('Login failed: ' + error.message);
            });
        }

        function getUserInfo() {
            apiCall('/api/auth/me', 'GET')
                .then(data => {
                    showUserSection(data.user);
                });
        }

        function showLoginSection() {
            document.getElementById('loginSection').style.display = 'block';
            document.getElementById('userSection').style.display = 'none';
        }

        function showUserSection(user) {
            document.getElementById('loginSection').style.display = 'none';
            document.getElementById('userSection').style.display = 'block';
            
            const userInfo = `User ID: ${user.userId}
Username: ${user.username}
Email: ${user.email}
Full Name: ${user.fullName}
Roles: ${user.roles ? user.roles.join(', ') : 'None'}
Tenant: ${user.tenantId || 'None'}`;
            
            document.getElementById('userInfo').textContent = userInfo;
        }

        function apiCall(endpoint, method = 'GET', body = null) {
            const token = localStorage.getItem('access_token');
            
            const options = {
                method: method,
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            };
            
            if (body) {
                options.body = JSON.stringify(body);
            }
            
            return fetch(config.planningServiceUrl + endpoint, options)
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                })
                .catch(error => {
                    showError(error.message);
                    throw error;
                });
        }

        function testUserInfo() {
            apiCall('/api/auth/me')
                .then(data => showResponse(data))
                .catch(() => {});
        }

        function testRetirementPlan() {
            // Get current user's retirement plan
            const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}');
            const userId = userInfo.userId || '1001'; // Fallback for demo
            
            apiCall(`/api/planning/retirement-plan/${userId}`)
                .then(data => showResponse(data))
                .catch(() => {});
        }

        function testRoles() {
            apiCall('/api/auth/roles')
                .then(data => showResponse(data))
                .catch(() => {});
        }

        function testAdminEndpoint() {
            apiCall('/api/auth/claims')
                .then(data => showResponse(data))
                .catch(() => {});
        }

        function showResponse(data) {
            document.getElementById('apiResponse').textContent = JSON.stringify(data, null, 2);
        }

        function showError(message) {
            document.getElementById('apiResponse').innerHTML = `<span class="error">Error: ${message}</span>`;
        }

        function logout() {
            const token = localStorage.getItem('access_token');
            
            // Clear local storage
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('user_info');
            
            // Redirect to Keycloak logout
            const logoutUrl = `${config.keycloakUrl}/realms/${config.realm}/protocol/openid-connect/logout?` +
                `redirect_uri=${encodeURIComponent('http://localhost:8000')}`;
            
            window.location.href = logoutUrl;
        }
    </script>
</body>
</html>
```

### Option 2: React Integration

For React applications, here's how to integrate:

```javascript
// Install: npm install keycloak-js

import Keycloak from 'keycloak-js';

const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'finova',
  clientId: 'finova-frontend'
};

const keycloak = new Keycloak(keycloakConfig);

// Initialize Keycloak
keycloak.init({
  onLoad: 'login-required',
  checkLoginIframe: false,
  pkceMethod: 'S256'
}).then((authenticated) => {
  if (authenticated) {
    console.log('User authenticated');
    // Store token for API calls
    localStorage.setItem('access_token', keycloak.token);
  }
}).catch((error) => {
  console.error('Keycloak initialization failed', error);
});

// API call wrapper
const apiCall = async (endpoint, options = {}) => {
  if (keycloak.token) {
    // Refresh token if needed
    await keycloak.updateToken(30);
    
    const response = await fetch(`http://localhost:8083${endpoint}`, {
      ...options,
      headers: {
        'Authorization': `Bearer ${keycloak.token}`,
        'Content-Type': 'application/json',
        ...options.headers
      }
    });
    
    return response.json();
  }
};
```

## 📋 Required Files for Frontend

Create these files in your frontend project:

### 1. `/callback.html` (OAuth callback handler)
```html
<!DOCTYPE html>
<html>
<head>
    <title>SSO Callback</title>
</head>
<body>
    <div>Processing login...</div>
    <script>
        // This page handles the OAuth callback
        // The main page JavaScript will process the authorization code
        if (window.parent !== window) {
            window.parent.postMessage(window.location.href, '*');
        } else {
            // Redirect back to main page with the authorization code
            window.location.href = '/';
        }
    </script>
</body>
</html>
```

### 2. Frontend Environment Configuration

```javascript
// config.js
export const SSO_CONFIG = {
  // Keycloak server
  keycloakUrl: 'http://localhost:8080',
  realm: 'finova',
  
  // Frontend client (public client)
  clientId: 'finova-frontend',
  redirectUri: 'http://localhost:8000/callback.html',
  
  // Backend service
  planningServiceUrl: 'http://localhost:8083',
  
  // OAuth scopes
  scope: 'openid profile email roles'
};
```

## 🚀 Quick Test Setup

### Step 1: Create Test HTML File
Save the HTML code above as `index.html` in your frontend project.

### Step 2: Create Callback Handler
Save the callback HTML as `callback.html`.

### Step 3: Start Your Frontend Server
```bash
# If using a simple HTTP server
python -m http.server 8000
# or
npx serve -p 8000
# or
php -S localhost:8000
```

### Step 4: Start Keycloak and Planning Service
```bash
# Start Keycloak
docker-compose -f docker-compose-keycloak.yml up -d

# Start Planning Service
cd planning-service
./mvnw spring-boot:run
```

### Step 5: Test the Flow
1. Go to http://localhost:8000
2. Click "Login with SSO"
3. Login with demo users (john.doe/password123)
4. Test API calls

## 🔄 Authentication Flow

```
Frontend (8000) → Keycloak (8080) → Frontend (8000) → Planning Service (8083)
      ↓               ↓                    ↓                    ↓
   1. Login      2. Authenticate      3. Get Token       4. API Calls
   Request       User Credentials     & User Info        with JWT
```

## 🛠️ Integration with Your Existing Frontend

If you already have a frontend framework, integrate like this:

### Vue.js Example:
```javascript
// main.js
import { createApp } from 'vue'
import App from './App.vue'
import { initKeycloak } from './auth/keycloak'

initKeycloak().then(() => {
  createApp(App).mount('#app')
})
```

### Angular Example:
```typescript
// app.module.ts
import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'finova',
        clientId: 'finova-frontend'
      },
      initOptions: {
        onLoad: 'login-required'
      }
    });
}

@NgModule({
  providers: [{
    provide: APP_INITIALIZER,
    useFactory: initializeKeycloak,
    multi: true,
    deps: [KeycloakService]
  }]
})
export class AppModule { }
```

## 🧪 Testing Checklist

- [ ] Frontend redirects to Keycloak login
- [ ] User can login with demo credentials
- [ ] JWT token is stored and used for API calls
- [ ] Protected endpoints require authentication
- [ ] Role-based access works correctly
- [ ] Logout clears tokens and redirects
- [ ] Token refresh works (optional)

This setup provides a complete SSO integration for your frontend running on port 8000! 🎉
