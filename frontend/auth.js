// ==========================================================================
// Finova Retirement - SSO Authentication Module
// Handles OAuth 2.0/OIDC integration with Keycloak
// ==========================================================================

// SSO Configuration
const SSO_CONFIG = {
    keycloakUrl: 'http://localhost:9080',
    realm: 'finova',
    clientId: 'finova-frontend',
    redirectUri: window.location.origin + '/callback.html',
    
    // Scopes to request
    scope: 'openid profile email roles',
    
    // Token storage keys
    storageKeys: {
        accessToken: 'finova_access_token',
        refreshToken: 'finova_refresh_token',
        userInfo: 'finova_user_info',
        tokenExpiry: 'finova_token_expiry'
    }
};

// Application State
let currentUser = null;
let authInitialized = false;

// ==========================================================================
// Authentication Functions
// ==========================================================================

/**
 * Initialize SSO authentication
 */
async function initializeAuth() {
    console.log('🔐 Initializing SSO authentication...');
    
    try {
        // Check if we have a valid token
        const token = getStoredToken();
        if (token && !isTokenExpired()) {
            // Validate token with backend
            const isValid = await validateTokenWithBackend(token);
            if (isValid) {
                const userInfo = await getUserInfoFromBackend(token);
                if (userInfo) {
                    setCurrentUser(userInfo);
                    updateUIForAuthenticatedUser();
                    authInitialized = true;
                    console.log('✅ User already authenticated:', currentUser.username);
                    return true;
                }
            }
        }
        
        // Check if we're returning from OAuth callback
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const state = urlParams.get('state');
        
        if (code) {
            console.log('🔄 Processing OAuth callback...');
            await handleOAuthCallback(code, state);
            return true;
        }
        
        // No valid authentication found
        console.log('❌ No valid authentication found');
        updateUIForUnauthenticatedUser();
        authInitialized = true;
        return false;
        
    } catch (error) {
        console.error('Auth initialization error:', error);
        updateUIForUnauthenticatedUser();
        authInitialized = true;
        return false;
    }
}

/**
 * Login with SSO
 */
function loginWithSSO() {
    console.log('🚀 Redirecting to SSO login...');
    
    // Generate state for security
    const state = generateRandomString(32);
    sessionStorage.setItem('oauth_state', state);
    
    // Build authorization URL
    const authUrl = `${SSO_CONFIG.keycloakUrl}/realms/${SSO_CONFIG.realm}/protocol/openid-connect/auth?` +
        `client_id=${SSO_CONFIG.clientId}&` +
        `redirect_uri=${encodeURIComponent(SSO_CONFIG.redirectUri)}&` +
        `response_type=code&` +
        `scope=${encodeURIComponent(SSO_CONFIG.scope)}&` +
        `state=${state}`;
    
    window.location.href = authUrl;
}

/**
 * Handle OAuth callback
 */
async function handleOAuthCallback(code, state) {
    console.log('📝 [DEBUG] handleOAuthCallback started', { code: code?.substring(0, 10) + '...', state });
    
    // Verify state to prevent CSRF attacks
    const storedState = sessionStorage.getItem('oauth_state');
    if (!state || state !== storedState) {
        console.error('❌ [ERROR] State verification failed', { provided: state, stored: storedState });
        throw new Error('Invalid state parameter - possible CSRF attack');
    }
    console.log('✅ [DEBUG] State verification passed');
    
    sessionStorage.removeItem('oauth_state');
    
    try {
        // Exchange authorization code for tokens
        console.log('🔄 [DEBUG] Starting token exchange...');
        const tokens = await exchangeCodeForTokens(code);
        console.log('✅ [DEBUG] Token exchange successful', { 
            hasAccessToken: !!tokens.access_token, 
            hasRefreshToken: !!tokens.refresh_token,
            expiresIn: tokens.expires_in 
        });
        
        // Store tokens
        console.log('💾 [DEBUG] Storing tokens...');
        storeTokens(tokens);
        console.log('✅ [DEBUG] Tokens stored successfully');
        
        // Get user information
        console.log('👤 [DEBUG] Getting user info from backend...');
        const userInfo = await getUserInfoFromBackend(tokens.access_token);
        console.log('📝 [DEBUG] User info response:', userInfo);
        
        if (userInfo) {
            console.log('✅ [DEBUG] User info received, setting current user...');
            setCurrentUser(userInfo);
            updateUIForAuthenticatedUser();
            
            // Clean up URL
            window.history.replaceState({}, document.title, window.location.pathname);
            
            console.log('✅ Authentication successful:', currentUser.username);
            
            // Reload the dashboard with authenticated user
            if (typeof loadDashboardData === 'function') {
                console.log('📊 [DEBUG] Loading dashboard data...');
                await loadDashboardData();
            } else {
                console.log('📊 [DEBUG] loadDashboardData function not available');
            }
            console.log('✅ [DEBUG] handleOAuthCallback completed successfully');
        } else {
            console.error('❌ [ERROR] No user info received from backend');
            throw new Error('Failed to get user information from backend');
        }
        
    } catch (error) {
        console.error('❌ [ERROR] OAuth callback error:', error);
        console.error('❌ [ERROR] Error stack:', error.stack);
        updateUIForUnauthenticatedUser();
        throw error;
    }
}

/**
 * Exchange authorization code for tokens
 */
async function exchangeCodeForTokens(code) {
    const tokenUrl = `${SSO_CONFIG.keycloakUrl}/realms/${SSO_CONFIG.realm}/protocol/openid-connect/token`;
    
    const params = new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: SSO_CONFIG.clientId,
        code: code,
        redirect_uri: SSO_CONFIG.redirectUri
    });
    
    const response = await fetch(tokenUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    });
    
    if (!response.ok) {
        throw new Error(`Token exchange failed: ${response.status} ${response.statusText}`);
    }
    
    const tokens = await response.json();
    console.log('🎫 Tokens received successfully');
    return tokens;
}

/**
 * Get user info from backend
 */
async function getUserInfoFromBackend(token) {
    try {
        const response = await fetch('http://localhost:8081/api/auth/me', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const authResponse = await response.json();
            return authResponse.user;
        } else if (response.status === 401) {
            console.warn('Token is invalid or expired');
            clearStoredAuth();
            return null;
        } else {
            throw new Error(`Failed to get user info: ${response.status}`);
        }
    } catch (error) {
        console.error('Error getting user info:', error);
        return null;
    }
}

/**
 * Validate token with backend
 */
async function validateTokenWithBackend(token) {
    try {
        const response = await fetch('http://localhost:8081/api/auth/validate', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const result = await response.json();
            return result.valid;
        }
        return false;
    } catch (error) {
        console.error('Token validation error:', error);
        return false;
    }
}

/**
 * Logout
 */
function logout() {
    console.log('🚪 Logging out...');
    
    // Clear stored authentication
    clearStoredAuth();
    setCurrentUser(null);
    updateUIForUnauthenticatedUser();
    
    // Optional: Logout from Keycloak in the background (no redirect)
    // This invalidates the SSO session without redirecting the user
    const token = getStoredToken();
    if (token) {
        // Call Keycloak logout endpoint in background to invalidate SSO session
        const logoutUrl = `${SSO_CONFIG.keycloakUrl}/realms/${SSO_CONFIG.realm}/protocol/openid-connect/logout`;
        fetch(logoutUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).catch(error => {
            console.log('SSO logout failed (this is normal if already logged out):', error);
        });
    }
    
    console.log('✅ Logged out successfully - staying on frontend');
}

// ==========================================================================
// Token Management
// ==========================================================================

function storeTokens(tokens) {
    localStorage.setItem(SSO_CONFIG.storageKeys.accessToken, tokens.access_token);
    if (tokens.refresh_token) {
        localStorage.setItem(SSO_CONFIG.storageKeys.refreshToken, tokens.refresh_token);
    }
    
    // Calculate expiry time
    const expiryTime = Date.now() + (tokens.expires_in * 1000);
    localStorage.setItem(SSO_CONFIG.storageKeys.tokenExpiry, expiryTime.toString());
}

function getStoredToken() {
    return localStorage.getItem(SSO_CONFIG.storageKeys.accessToken);
}

function isTokenExpired() {
    const expiryTime = localStorage.getItem(SSO_CONFIG.storageKeys.tokenExpiry);
    if (!expiryTime) return true;
    
    return Date.now() >= parseInt(expiryTime);
}

function clearStoredAuth() {
    Object.values(SSO_CONFIG.storageKeys).forEach(key => {
        localStorage.removeItem(key);
    });
}

// ==========================================================================
// User Management
// ==========================================================================

function setCurrentUser(user) {
    currentUser = user;
    if (user) {
        localStorage.setItem(SSO_CONFIG.storageKeys.userInfo, JSON.stringify(user));
    }
}

function getCurrentUser() {
    return currentUser;
}

function isAuthenticated() {
    return currentUser !== null && !isTokenExpired();
}

function hasRole(role) {
    return currentUser && currentUser.roles && currentUser.roles.includes(role);
}

function canAccessUser(userId) {
    if (!currentUser) return false;
    
    // User can access their own data
    if (currentUser.userId.toString() === userId.toString()) {
        return true;
    }
    
    // Admin and financial advisor can access other users
    return hasRole('ADMIN') || hasRole('FINANCIAL_ADVISOR') || hasRole('SYSTEM_ADMIN');
}

// ==========================================================================
// API Integration
// ==========================================================================

/**
 * Wrapper for authenticated API calls
 */
async function authenticatedFetch(url, options = {}) {
    const token = getStoredToken();
    
    if (!token || isTokenExpired()) {
        throw new Error('No valid authentication token');
    }
    
    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    const response = await fetch(url, {
        ...options,
        headers
    });
    
    if (response.status === 401) {
        // Token is invalid, clear auth and redirect to login
        clearStoredAuth();
        setCurrentUser(null);
        updateUIForUnauthenticatedUser();
        throw new Error('Authentication required');
    }
    
    return response;
}

// ==========================================================================
// UI Updates
// ==========================================================================

function updateUIForAuthenticatedUser() {
    console.log('🎨 Updating UI for authenticated user');
    
    // Update header to show user info and logout option
    updateHeaderForAuthenticatedUser();
    
    // Initialize section visibility properly for CSS-based navigation
    const sections = ['dashboard-section', 'accounts-section', 'planning-section', 'services-section'];
    sections.forEach(sectionId => {
        const section = document.getElementById(sectionId);
        if (section) {
            section.style.display = ''; // Clear inline display style
            // Only dashboard should be active initially
            if (sectionId === 'dashboard-section') {
                section.classList.add('active');
            } else {
                section.classList.remove('active');
            }
        }
    });
    
    // Ensure dashboard nav link is active
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        if (link.dataset.section === 'dashboard') {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
    
    // Hide login section if it exists
    const loginSection = document.getElementById('login-section');
    if (loginSection) {
        loginSection.style.display = 'none';
    }
    
    // Enable navigation
    navLinks.forEach(link => {
        link.style.pointerEvents = 'auto';
        link.style.opacity = '1';
    });
}

function updateUIForUnauthenticatedUser() {
    console.log('🎨 Updating UI for unauthenticated user');
    
    // Update header to show login option
    updateHeaderForUnauthenticatedUser();
    
    // Hide main application content and show login prompt
    showLoginPrompt();
    
    // Disable navigation
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.style.pointerEvents = 'none';
        link.style.opacity = '0.5';
    });
}

function updateHeaderForAuthenticatedUser() {
    const headerActions = document.querySelector('.header-actions');
    if (headerActions && currentUser) {
        headerActions.innerHTML = `
            <div class="user-info">
                <span class="user-greeting">Welcome, ${currentUser.fullName || currentUser.username}</span>
                <div class="user-menu">
                    <button class="user-menu-btn" onclick="toggleUserMenu()">
                        <i class="fas fa-user-circle"></i>
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="user-dropdown" id="user-dropdown">
                        <div class="user-dropdown-item">
                            <strong>${currentUser.fullName || currentUser.username}</strong><br>
                            <small>${currentUser.email}</small>
                        </div>
                        <div class="user-dropdown-divider"></div>
                        <div class="user-dropdown-item">
                            <strong>Roles:</strong> ${currentUser.roles ? currentUser.roles.join(', ') : 'None'}
                        </div>
                        <div class="user-dropdown-divider"></div>
                        <button class="user-dropdown-item logout-btn" onclick="logout()">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </button>
                    </div>
                </div>
            </div>
        `;
    }
}

function updateHeaderForUnauthenticatedUser() {
    const headerActions = document.querySelector('.header-actions');
    if (headerActions) {
        headerActions.innerHTML = `
            <button class="login-btn" onclick="loginWithSSO()">
                <i class="fas fa-sign-in-alt"></i> Login with SSO
            </button>
        `;
    }
}

function showLoginPrompt() {
    // Hide all main sections
    const sections = ['dashboard-section', 'accounts-section', 'planning-section', 'services-section'];
    sections.forEach(sectionId => {
        const section = document.getElementById(sectionId);
        if (section) {
            section.style.display = 'none';
        }
    });
    
    // Create and show login section
    let loginSection = document.getElementById('login-section');
    if (!loginSection) {
        loginSection = document.createElement('div');
        loginSection.id = 'login-section';
        loginSection.className = 'section active';
        
        document.body.appendChild(loginSection);
    }
    
    loginSection.innerHTML = `
        <div class="login-container">
            <div class="login-card">
                <div class="login-header">
                    <div class="logo-large">
                        <div class="logo-icon">F</div>
                        <h1>Finova Retirement</h1>
                    </div>
                    <p>Please login to access your retirement planning dashboard</p>
                </div>
                <div class="login-content">
                    <button class="sso-login-btn" onclick="loginWithSSO()">
                        <i class="fas fa-shield-alt"></i>
                        Login with Single Sign-On
                    </button>
                    <div class="login-features">
                        <div class="feature-item">
                            <i class="fas fa-lock"></i>
                            <span>Secure Authentication</span>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-chart-line"></i>
                            <span>Personalized Planning</span>
                        </div>
                        <div class="feature-item">
                            <i class="fas fa-shield-check"></i>
                            <span>Role-Based Access</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    loginSection.style.display = 'block';
}

function toggleUserMenu() {
    const dropdown = document.getElementById('user-dropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
    }
}

// Close user menu when clicking outside
document.addEventListener('click', function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.getElementById('user-dropdown');
    
    if (userMenu && dropdown && !userMenu.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// ==========================================================================
// Utility Functions
// ==========================================================================

function generateRandomString(length) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

// ==========================================================================
// Global Exports
// ==========================================================================

// Make functions globally available
window.initializeAuth = initializeAuth;
window.loginWithSSO = loginWithSSO;
window.logout = logout;
window.isAuthenticated = isAuthenticated;
window.getCurrentUser = getCurrentUser;
window.hasRole = hasRole;
window.canAccessUser = canAccessUser;
window.authenticatedFetch = authenticatedFetch;
window.toggleUserMenu = toggleUserMenu;

console.log('🔐 SSO Authentication module loaded!');
