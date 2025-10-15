# SSO Demo Setup Guide

This guide will help you set up and test the complete SSO functionality for your demo.

## 🚀 Quick Setup (5 minutes)

### Step 1: Start Keycloak
```bash
# In the planning-service directory
docker-compose -f docker-compose-keycloak.yml up -d
```

### Step 2: Wait for Keycloak to start
- Wait ~30 seconds for Keycloak to fully start
- Check: http://localhost:8080 should show Keycloak welcome page

### Step 3: Import Demo Configuration
1. Go to: http://localhost:8080/admin
2. Login: `admin` / `admin123`
3. Import realm: Use the file `keycloak-data/finova-realm.json`

### Step 4: Start Planning Service
```bash
./mvnw spring-boot:run
```

## 👥 Demo Users

| Username | Password | Role | User ID | Description |
|----------|----------|------|---------|-------------|
| `john.doe` | `password123` | USER | 1001 | Regular user |
| `jane.admin` | `admin123` | ADMIN | 1002 | Administrator |
| `advisor.mike` | `advisor123` | FINANCIAL_ADVISOR | 1003 | Financial Advisor |

## 🧪 Testing SSO

### 1. Test Without Authentication (Should Fail)
```bash
curl http://localhost:8083/api/planning/retirement-plan/1001
# Expected: 401 Unauthorized
```

### 2. Get JWT Token
```bash
# Get token for john.doe (regular user)
curl -X POST "http://localhost:8080/realms/finova/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=finova-planning-service" \
  -d "client_secret=finova-planning-secret-2024" \
  -d "username=john.doe" \
  -d "password=password123"

# Save the access_token from response
```

### 3. Test Authenticated Requests
```bash
# Replace {TOKEN} with your actual token
export TOKEN="your-jwt-token-here"

# Test user info endpoint
curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/auth/me

# Test user's own retirement plan (should work)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/planning/retirement-plan/1001

# Test accessing another user's data (should fail with 403)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/planning/retirement-plan/1002
```

### 4. Test Admin Access
```bash
# Get admin token
curl -X POST "http://localhost:8080/realms/finova/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=finova-planning-service" \
  -d "client_secret=finova-planning-secret-2024" \
  -d "username=jane.admin" \
  -d "password=admin123"

export ADMIN_TOKEN="admin-jwt-token-here"

# Admin should access any user's data
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8083/api/planning/retirement-plan/1001
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8083/api/planning/retirement-plan/1002
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8083/api/planning/retirement-plan/1003
```

### 5. Test Role-Based Access
```bash
# Test admin-only endpoints
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8083/api/auth/claims
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8083/api/auth/config

# These should fail with regular user token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/auth/claims
```

## 🎯 Demo Flow for Stakeholders

### Scenario 1: Regular User Access
1. **Show login**: Get token for `john.doe`
2. **Show access**: Can access own data (user ID 1001)
3. **Show security**: Cannot access other user's data (403 error)

### Scenario 2: Admin Override
1. **Show admin login**: Get token for `jane.admin`
2. **Show admin power**: Can access any user's data
3. **Show admin endpoints**: Can access admin-only features

### Scenario 3: Financial Advisor
1. **Show advisor login**: Get token for `advisor.mike`
2. **Show advisor access**: Can access client data
3. **Show role-based security**: Different permissions than regular users

## 🔧 Frontend Integration

For frontend applications, use these endpoints:

### Login Flow
```javascript
// Redirect to Keycloak login
window.location.href = 'http://localhost:8080/realms/finova/protocol/openid-connect/auth?client_id=finova-frontend&redirect_uri=http://localhost:8000/callback&response_type=code&scope=openid'
```

### API Calls
```javascript
// Include token in all API requests
const token = localStorage.getItem('access_token');

fetch('http://localhost:8083/api/planning/retirement-plan/1001', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

## 🛠️ Troubleshooting

### Common Issues:

1. **"Unable to resolve Configuration"**
   - Ensure Keycloak is running on port 8080
   - Check realm is correctly imported

2. **"401 Unauthorized"**
   - Check JWT token is valid and not expired
   - Verify token format: `Authorization: Bearer {token}`

3. **"403 Forbidden"**
   - User doesn't have permission for that resource
   - Check user roles and user ID matching

4. **CORS Errors**
   - Frontend origin should be in allowed origins list
   - Currently configured for localhost:8000, 3000, 4200

## 📱 Mobile/Postman Testing

Import this Postman collection for easy testing:

### Environment Variables:
- `keycloak_url`: `http://localhost:8080`
- `planning_service_url`: `http://localhost:8083`
- `client_id`: `finova-planning-service`
- `client_secret`: `finova-planning-secret-2024`

## 🎭 Demo Script

Here's a suggested demo script:

1. **"Here's our planning service without authentication"**
   - Show 401 error when accessing protected endpoints

2. **"Now with SSO - users must authenticate"**
   - Get token, show successful authentication

3. **"Role-based security - users can only access their own data"**
   - Show user accessing own data vs. getting 403 for others

4. **"Admins have elevated access"**
   - Show admin accessing any user's data

5. **"Enterprise features like audit trails"**
   - Show JWT claims, user info, roles

This demonstrates enterprise-grade security suitable for financial applications! 🏦

## 🧹 Cleanup

After demo:
```bash
# Stop services
docker-compose -f docker-compose-keycloak.yml down

# Clean up volumes (optional)
docker-compose -f docker-compose-keycloak.yml down -v
```
