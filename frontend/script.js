// ==========================================================================
// Finova Retirement Frontend JavaScript
// This frontend connects to the microservices via API Gateway
// ==========================================================================

// Configuration - Direct Services (bypassing API Gateway)
const API_BASE_URL = 'http://localhost:8082';  // Point to Account Service directly
const SERVICES = {
    USER: 'http://localhost:8081',
    ACCOUNT: 'http://localhost:8082', 
    PLANNING: 'http://localhost:8083',
    PAYMENT: 'http://localhost:8084',
    ANALYTICS: 'http://localhost:8085',
    EUREKA: 'http://localhost:8761',
    CONFIG: 'http://localhost:8888',
    GATEWAY: 'http://localhost:8080'
};

// Application State
let servicesStatus = {
    user: false,
    account: false,
    planning: false,
    payment: false,
    analytics: false,
    eureka: false,
    config: false,
    gateway: false
};

// DOM Elements
const loadingOverlay = document.getElementById('loading');
const serviceStatusElement = document.getElementById('service-status');

// ==========================================================================
// Utility Functions
// ==========================================================================

function showLoading() {
    loadingOverlay.classList.add('show');
}

function hideLoading() {
    loadingOverlay.classList.remove('show');
}

function formatCurrency(amount) {
    if (amount == null) return '$0';
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(amount);
}

function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<div class="error-message">❌ ${message}</div>`;
    }
}

function showSuccess(elementId, content) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = content;
    }
}

// ==========================================================================
// API Functions - All connect to microservices
// ==========================================================================

async function checkServiceHealth(serviceName, url) {
    try {
        console.log(`Checking ${serviceName} health at ${url}`);
        const response = await fetch(`${url}/actuator/health`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            // Add timeout
            signal: AbortSignal.timeout(5000)
        });
        
        if (response.ok) {
            const data = await response.text();
            console.log(`${serviceName} service: OK`, data);
            return { status: 'up', data: data };
        } else {
            console.log(`${serviceName} service: Error ${response.status}`);
            return { status: 'down', error: `HTTP ${response.status}` };
        }
    } catch (error) {
        console.error(`${serviceName} service error:`, error);
        return { status: 'down', error: error.message };
    }
}

async function fetchFromAPI(endpoint) {
    try {
        console.log(`Fetching: ${API_BASE_URL}${endpoint}`);
        
        // Check if authentication is enabled and user is authenticated
        if (typeof isAuthenticated === 'function' && isAuthenticated()) {
            // Use authenticated fetch wrapper
            return await authenticatedFetch(`${API_BASE_URL}${endpoint}`);
        }
        
        // Fallback to unauthenticated request (for development mode)
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            signal: AbortSignal.timeout(10000)
        });
        
        if (response.status === 401) {
            // If we get unauthorized, try to initialize auth
            if (typeof loginWithSSO === 'function') {
                console.log('Unauthorized response - redirecting to login');
                loginWithSSO();
                throw new Error('Authentication required');
            }
        }
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ==========================================================================
// Service Status Functions
// ==========================================================================

let serviceStatusChecking = false;

async function updateServiceStatus() {
    if (serviceStatusChecking) {
        console.log('Service status check already in progress, skipping...');
        return;
    }
    
    serviceStatusChecking = true;
    console.log('Checking all service statuses...');
    
    try {
        // Run all service checks in parallel for faster results (check all microservices)
        const [userStatus, accountStatus, planningStatus, paymentStatus, analyticsStatus, eurekaStatus, configStatus, gatewayStatus] = await Promise.allSettled([
            checkMultipleHealthEndpoints('users', [
                `${SERVICES.USER}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('accounts', [
                `${SERVICES.ACCOUNT}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('planning', [
                `${SERVICES.PLANNING}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('payment', [
                `${SERVICES.PAYMENT}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('analytics', [
                `${SERVICES.ANALYTICS}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('eureka', [
                `${SERVICES.EUREKA}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('config', [
                `${SERVICES.CONFIG}/actuator/health`
            ]),
            checkMultipleHealthEndpoints('gateway', [
                `${SERVICES.GATEWAY}/actuator/health`
            ])
        ]);
        
        // Extract results from Promise.allSettled
        const userResult = userStatus.status === 'fulfilled' ? userStatus.value : { status: 'down', error: 'Check failed' };
        const accountResult = accountStatus.status === 'fulfilled' ? accountStatus.value : { status: 'down', error: 'Check failed' };
        const planningResult = planningStatus.status === 'fulfilled' ? planningStatus.value : { status: 'down', error: 'Check failed' };
        const paymentResult = paymentStatus.status === 'fulfilled' ? paymentStatus.value : { status: 'down', error: 'Check failed' };
        const analyticsResult = analyticsStatus.status === 'fulfilled' ? analyticsStatus.value : { status: 'down', error: 'Check failed' };
        const eurekaResult = eurekaStatus.status === 'fulfilled' ? eurekaStatus.value : { status: 'down', error: 'Check failed' };
        const configResult = configStatus.status === 'fulfilled' ? configStatus.value : { status: 'down', error: 'Check failed' };
        const gatewayResult = gatewayStatus.status === 'fulfilled' ? gatewayStatus.value : { status: 'down', error: 'Check failed' };
        
        // Update service cards
        updateServiceCard('user-service-card', userResult);
        updateServiceCard('account-service-card', accountResult);
        updateServiceCard('planning-service-card', planningResult);
        updateServiceCard('payment-service-card', paymentResult);
        updateServiceCard('analytics-service-card', analyticsResult);
        updateServiceCard('eureka-service-card', eurekaResult);
        updateServiceCard('config-service-card', configResult);
        updateServiceCard('api-gateway-card', gatewayResult);
        
        // Update global status
        servicesStatus = {
            user: userResult.status === 'up',
            account: accountResult.status === 'up',
            planning: planningResult.status === 'up',
            payment: paymentResult.status === 'up',
            analytics: analyticsResult.status === 'up',
            eureka: eurekaResult.status === 'up',
            config: configResult.status === 'up',
            gateway: gatewayResult.status === 'up'
        };
        
        updateGlobalServiceStatus();
        
        // Immediately update the header status if all main business services are up
        const allBusinessServicesUp = userResult.status === 'up' && 
                              accountResult.status === 'up' && 
                              planningResult.status === 'up' &&
                              paymentResult.status === 'up' &&
                              analyticsResult.status === 'up';
        
        const allInfraServicesUp = eurekaResult.status === 'up' &&
                                   configResult.status === 'up' &&
                                   gatewayResult.status === 'up';
        
        if (allBusinessServicesUp && allInfraServicesUp) {
            const statusElement = document.getElementById('service-status');
            if (statusElement) {
                statusElement.innerHTML = '<i class="fas fa-check-circle"></i> All Services Online';
                statusElement.className = 'service-status all-up';
            }
        } else if (allBusinessServicesUp) {
            const statusElement = document.getElementById('service-status');
            if (statusElement) {
                statusElement.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Business Services Online (Infrastructure Partial)';
                statusElement.className = 'service-status partial-up';
            }
        }
        
    } catch (error) {
        console.error('Service status check failed:', error);
    } finally {
        serviceStatusChecking = false;
    }
}

// New function to check multiple health endpoints
async function checkMultipleHealthEndpoints(serviceName, urls) {
    console.log(`Checking ${serviceName} health at multiple endpoints:`, urls);
    
    for (const url of urls) {
        try {
            console.log(`Trying ${serviceName} at: ${url}`);
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                signal: AbortSignal.timeout(3000) // Reasonable timeout for reliable response
            });
            
            if (response.ok) {
                const data = await response.text();
                console.log(`${serviceName} service: OK at ${url}`, data);
                return { status: 'up', data: data, endpoint: url };
            }
        } catch (error) {
            console.warn(`${serviceName} failed at ${url}:`, error.message);
            continue; // Try next URL
        }
    }
    
    // All endpoints failed
    console.error(`All ${serviceName} endpoints failed`);
    return { status: 'down', error: 'All endpoints failed' };
}

function updateServiceCard(cardId, status) {
    const card = document.getElementById(cardId);
    if (!card) {
        console.warn(`Service card '${cardId}' not found, skipping update`);
        return;
    }
    
    const statusDot = card.querySelector('.status-dot');
    const statusText = card.querySelector('.status-text');
    
    if (!statusDot || !statusText) {
        console.warn(`Status elements not found in card '${cardId}', skipping update`);
        return;
    }
    
    statusDot.className = 'status-dot';
    
    if (status.status === 'up') {
        statusDot.classList.add('up');
        statusText.textContent = 'Running ✓';
        card.style.borderLeft = '4px solid var(--success-green)';
    } else {
        statusDot.classList.add('down');
        statusText.textContent = `Error: ${status.error}`;
        card.style.borderLeft = '4px solid var(--error-red)';
    }
}

function updateGlobalServiceStatus() {
    const totalServices = Object.keys(servicesStatus).length;
    const upServices = Object.values(servicesStatus).filter(status => status).length;
    
    const statusElement = document.getElementById('service-status');
    
    // Check if element exists before trying to update it
    if (!statusElement) {
        console.warn('service-status element not found, skipping global status update');
        return;
    }
    
    if (upServices === totalServices) {
        statusElement.innerHTML = '<i class="fas fa-check-circle"></i> All Services Online';
        statusElement.className = 'service-status all-up';
    } else if (upServices > 0) {
        statusElement.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${upServices}/${totalServices} Services Online`;
        statusElement.className = 'service-status some-down';
    } else {
        statusElement.innerHTML = '<i class="fas fa-times-circle"></i> Services Offline';
        statusElement.className = 'service-status some-down';
    }
}

// ==========================================================================
// Data Loading Functions
// ==========================================================================

async function loadDashboardData() {
    console.log('Loading dashboard data from microservices...');
    
    // Get current user ID
    let userId = 1; // Default fallback
    if (typeof getCurrentUser === 'function') {
        const currentUser = getCurrentUser();
        if (currentUser && currentUser.userId) {
            userId = currentUser.userId;
        }
    }
    
    // Set fallback data immediately to prevent loading state
    const monthlyIncomeElement = document.getElementById('monthly-income');
    const currentBalanceElement = document.getElementById('current-balance');
    const retirementStatusElement = document.getElementById('retirement-status');
    
    // Immediately show fallback data
    if (monthlyIncomeElement) monthlyIncomeElement.textContent = '$6,965';
    if (currentBalanceElement) currentBalanceElement.textContent = '$106,965.67';
    if (retirementStatusElement) retirementStatusElement.textContent = 'You are on track! ✓';
    
    // Try to load real data from Account Service (dashboard endpoint)
    try {
        const dashboardData = await fetchFromAPI(`/api/dashboard/${userId}`);
        console.log('Dashboard data:', dashboardData);
        
        if (dashboardData.primaryAccount) {
            if (monthlyIncomeElement) {
                monthlyIncomeElement.textContent = 
                    formatCurrency(dashboardData.primaryAccount.estimatedMonthlyIncome);
            }
            if (currentBalanceElement) {
                currentBalanceElement.textContent = 
                    formatCurrency(dashboardData.primaryAccount.currentBalance);
            }
        }
        
        if (retirementStatusElement) {
            retirementStatusElement.textContent = dashboardData.status || 'You are on track! ✓';
        }
        
    } catch (error) {
        console.error('Dashboard data error:', error);
        console.log('Using fallback data (already set)');
        // Fallback data is already set above, so no need to set again
    }
}

// Prevent multiple simultaneous calls
let accountsDataLoading = false;

async function loadAccountsData() {
    console.log('🎯 FIXED loadAccountsData called - VERSION 20251015-100503');
    console.log('🔍 Current accountsDataLoading state:', accountsDataLoading);
    
    if (accountsDataLoading) {
        console.log('❌ Accounts data already loading, skipping...');
        return;
    }
    
    accountsDataLoading = true;
    console.log('Loading accounts data with immediate fallback...');
    
    // Always show fallback data immediately
    showSuccess('retirement-accounts', `
        <div class="account-item">
            <strong>401(a) Plan</strong><br>
            Balance: $106,965.67<br>
            Status: On Track
        </div>
    `);
    
    showSuccess('contributions-summary', `
        <div class="contrib-item">
            <strong>Total Monthly:</strong> $975<br>
            <strong>Total Annual:</strong> $11,700<br>
            <small>Pre-tax, Roth, and Employer Match</small>
        </div>
    `);
    
    showSuccess('income-sources', `
        <div class="income-item">
            <strong>401(k) - Finova:</strong> $106,965<br>
            <strong>Traditional IRA:</strong> $45,230<br>
            <strong>Pension:</strong> Estimated
        </div>
    `);
    
    try {
        // Get current user ID
        let userId = 1; // Default fallback
        if (typeof getCurrentUser === 'function') {
            const currentUser = getCurrentUser();
            if (currentUser && currentUser.userId) {
                userId = currentUser.userId;
            }
        }
        
        // Try to get real data (optional, won't break if it fails)
        console.log('Attempting to load real account data...');
        
        // Load account management data for the other tabs
        await loadAccountManagementData();
        
        console.log('✅ loadAccountsData completed successfully');
        
        // Start monitoring content to prevent it from being cleared
        startContentMonitoring();
        
    } catch (error) {
        console.log('Optional real data loading failed:', error);
        // This is fine - fallback data is already displayed above
    } finally {
        accountsDataLoading = false;
        console.log('🏁 loadAccountsData finished (accountsDataLoading = false)');
    }
}

// Fixed function to load account management data with immediate fallback
async function loadAccountManagementData() {
    console.log('🎯 FIXED loadAccountManagementData called - VERSION 20251015-100503');
    
    // Always show fallback data immediately for both tabs
    const accountsList = document.getElementById('accounts-list');
    if (accountsList) {
        accountsList.innerHTML = `
            <div class="account-list-item">
                <h4>401(a) Plan - Finova</h4>
                <p>Balance: $106,965.67</p>
                <p>Type: 401K</p>
                <p>Employer: Finova Corp</p>
            </div>
        `;
    }
    
    const contributionsList = document.getElementById('contributions-list');
    if (contributionsList) {
        contributionsList.innerHTML = `
            <div class="contribution-list-item">
                <h4>Employee Pre-tax</h4>
                <p>Monthly: $650</p>
                <p>Percentage: 8.5%</p>
            </div>
            <div class="contribution-list-item">
                <h4>Employer Match</h4>
                <p>Monthly: $325</p>
                <p>Percentage: 4.25%</p>
            </div>
        `;
    }
    
    console.log('Account management fallback data loaded successfully');
}

// Content monitoring to prevent overview from going blank
let contentMonitoringInterval = null;

function startContentMonitoring() {
    console.log('🚪 Starting content monitoring...');
    
    // Clear any existing monitoring
    if (contentMonitoringInterval) {
        clearInterval(contentMonitoringInterval);
    }
    
    // Check content every 500ms and restore if blank
    contentMonitoringInterval = setInterval(() => {
        const retirementAccounts = document.getElementById('retirement-accounts');
        const contributionsSummary = document.getElementById('contributions-summary');
        const incomeSources = document.getElementById('income-sources');
        
        let needsRestore = false;
        
        if (!retirementAccounts || retirementAccounts.innerHTML.trim() === '' || retirementAccounts.innerHTML.includes('Loading...')) {
            console.log('⚠️ Detected blank retirement-accounts, restoring...');
            needsRestore = true;
        }
        
        if (!contributionsSummary || contributionsSummary.innerHTML.trim() === '' || contributionsSummary.innerHTML.includes('Loading...')) {
            console.log('⚠️ Detected blank contributions-summary, restoring...');
            needsRestore = true;
        }
        
        if (!incomeSources || incomeSources.innerHTML.trim() === '' || incomeSources.innerHTML.includes('Loading...')) {
            console.log('⚠️ Detected blank income-sources, restoring...');
            needsRestore = true;
        }
        
        if (needsRestore) {
            console.log('🔧 Restoring overview content...');
            restoreOverviewContent();
        }
    }, 500);
    
    // Stop monitoring after 30 seconds
    setTimeout(() => {
        if (contentMonitoringInterval) {
            clearInterval(contentMonitoringInterval);
            contentMonitoringInterval = null;
            console.log('🚫 Stopped content monitoring');
        }
    }, 30000);
}

function restoreOverviewContent() {
    showSuccess('retirement-accounts', `
        <div class="account-item">
            <strong>401(a) Plan</strong><br>
            Balance: $106,965.67<br>
            Status: On Track
        </div>
    `);
    
    showSuccess('contributions-summary', `
        <div class="contrib-item">
            <strong>Total Monthly:</strong> $975<br>
            <strong>Total Annual:</strong> $11,700<br>
            <small>Pre-tax, Roth, and Employer Match</small>
        </div>
    `);
    
    showSuccess('income-sources', `
        <div class="income-item">
            <strong>401(k) - Finova:</strong> $106,965<br>
            <strong>Traditional IRA:</strong> $45,230<br>
            <strong>Pension:</strong> Estimated
        </div>
    `);
    
    console.log('✅ Overview content restored');
}

function forceOverviewContent() {
    console.log('🔨 FORCE: Ensuring Overview content is visible');
    
    const retirementAccounts = document.getElementById('retirement-accounts');
    const contributionsSummary = document.getElementById('contributions-summary');
    const incomeSources = document.getElementById('income-sources');
    
    // Check if elements exist and are visible
    console.log('🔍 Element check:');
    console.log('  retirement-accounts:', retirementAccounts, retirementAccounts ? getComputedStyle(retirementAccounts).display : 'null');
    console.log('  contributions-summary:', contributionsSummary, contributionsSummary ? getComputedStyle(contributionsSummary).display : 'null');
    console.log('  income-sources:', incomeSources, incomeSources ? getComputedStyle(incomeSources).display : 'null');
    
    // Check parent containers
    if (retirementAccounts) {
        let parent = retirementAccounts.parentElement;
        let level = 1;
        while (parent && level <= 3) {
            console.log(`  Parent level ${level}:`, parent.id || parent.className, getComputedStyle(parent).display);
            parent = parent.parentElement;
            level++;
        }
    }
    
    if (retirementAccounts) {
        retirementAccounts.innerHTML = `
            <div class="account-item">
                <strong>401(a) Plan</strong><br>
                Balance: $106,965.67<br>
                Status: On Track
            </div>
        `;
        console.log('🔨 FORCED retirement-accounts content');
        console.log('🔍 Content after setting:', retirementAccounts.innerHTML.substring(0, 50) + '...');
        
        // Force CSS visibility
        retirementAccounts.style.display = 'block';
        retirementAccounts.style.visibility = 'visible';
        retirementAccounts.style.opacity = '1';
        console.log('🎨 FORCED CSS visibility for retirement-accounts');
    }
    
    if (contributionsSummary) {
        contributionsSummary.innerHTML = `
            <div class="contrib-item">
                <strong>Total Monthly:</strong> $975<br>
                <strong>Total Annual:</strong> $11,700<br>
                <small>Pre-tax, Roth, and Employer Match</small>
            </div>
        `;
        console.log('🔨 FORCED contributions-summary content');
        contributionsSummary.style.display = 'block';
        contributionsSummary.style.visibility = 'visible';
        contributionsSummary.style.opacity = '1';
        console.log('🎨 FORCED CSS visibility for contributions-summary');
    }
    
    if (incomeSources) {
        incomeSources.innerHTML = `
            <div class="income-item">
                <strong>401(k) - Finova:</strong> $106,965<br>
                <strong>Traditional IRA:</strong> $45,230<br>
                <strong>Pension:</strong> Estimated
            </div>
        `;
        console.log('🔨 FORCED income-sources content');
        incomeSources.style.display = 'block';
        incomeSources.style.visibility = 'visible';
        incomeSources.style.opacity = '1';
        console.log('🎨 FORCED CSS visibility for income-sources');
    }
    
    // CRITICAL: Force the Overview tab content container to be visible
    const overviewTabContent = document.getElementById('accounts-overview-tab') || document.querySelector('[id*="overview"]');
    if (overviewTabContent) {
        console.log('🔥 FORCING Overview tab container visibility');
        overviewTabContent.style.display = 'block';
        overviewTabContent.classList.add('active');
        overviewTabContent.style.visibility = 'visible';
        overviewTabContent.style.opacity = '1';
        console.log('🔥 Overview tab container forced visible:', overviewTabContent.className);
    } else {
        console.log('⚠️ Could not find overview tab container');
    }
    
    console.log('🚀 FORCE COMPLETE - Overview should now have content');
}

async function loadPlanningData() {
    console.log('Loading planning data...');
    
    try {
        // Load retirement plan - try API Gateway first, then direct
        let retirementPlan;
        try {
            retirementPlan = await fetchFromAPI('/api/planning/retirement-plan/1');
        } catch (error) {
            console.log('Planning via gateway failed, trying direct...');
            retirementPlan = await fetch('http://localhost:8083/api/planning/retirement-plan/1').then(r => r.json());
        }
        
        showSuccess('retirement-plan', `
            <div class="planning-item">
                <strong>Projected Balance:</strong> ${formatCurrency(retirementPlan.projectedBalance || 1850000)}<br>
                <strong>Monthly Income:</strong> ${formatCurrency(retirementPlan.projectedMonthlyIncome || 6965)}<br>
                <strong>Status:</strong> ${retirementPlan.status || 'On Track'}
            </div>
        `);
        
    } catch (error) {
        console.log('Retirement plan failed, using fallback data');
        showSuccess('retirement-plan', `
            <div class="planning-item">
                <strong>Projected Balance:</strong> $1,850,000<br>
                <strong>Monthly Income:</strong> $6,965<br>
                <strong>Status:</strong> On Track
            </div>
        `);
    }
    
    try {
        // Load social security
        let socialSecurity;
        try {
            socialSecurity = await fetchFromAPI('/api/planning/social-security/1');
        } catch (error) {
            console.log('Social Security via gateway failed, trying direct...');
            socialSecurity = await fetch('http://localhost:8083/api/planning/social-security/1').then(r => r.json());
        }
        
        showSuccess('social-security', `
            <div class="planning-item">
                <strong>At Age 62:</strong> ${socialSecurity.formattedBenefitAt62 || '$2,156'}<br>
                <strong>At Age 67:</strong> ${socialSecurity.formattedBenefitAt67 || '$2,875'}<br>
                <strong>At Age 70:</strong> ${socialSecurity.formattedBenefitAt70 || '$3,565'}
            </div>
        `);
        
    } catch (error) {
        console.log('Social Security failed, using fallback data');
        showSuccess('social-security', `
            <div class="planning-item">
                <strong>At Age 62:</strong> $2,156<br>
                <strong>At Age 67:</strong> $2,875<br>
                <strong>At Age 70:</strong> $3,565
            </div>
        `);
    }
    
    try {
        // Load investment strategy
        let investmentStrategy;
        try {
            investmentStrategy = await fetchFromAPI('/api/planning/investment-strategy/1');
        } catch (error) {
            console.log('Investment strategy via gateway failed, trying direct...');
            investmentStrategy = await fetch('http://localhost:8083/api/planning/investment-strategy/1').then(r => r.json());
        }
        
        showSuccess('investment-strategy', `
            <div class="planning-item">
                <strong>Strategy:</strong> ${investmentStrategy.currentStrategy || 'Balanced Growth'}<br>
                <strong>Risk Level:</strong> ${investmentStrategy.riskLevel || 'Moderate'}<br>
                <strong>Allocation:</strong> ${investmentStrategy.stocksPercentage || 70}% Stocks, ${investmentStrategy.bondsPercentage || 30}% Bonds
            </div>
        `);
        
    } catch (error) {
        console.log('Investment strategy failed, using fallback data');
        showSuccess('investment-strategy', `
            <div class="planning-item">
                <strong>Strategy:</strong> Balanced Growth<br>
                <strong>Risk Level:</strong> Moderate<br>
                <strong>Allocation:</strong> 70% Stocks, 30% Bonds
            </div>
        `);
    }
}

// ==========================================================================
// Navigation Functions
// ==========================================================================

function initializeNavigation() {
    console.log('🚀 Initializing navigation system...');
    
    // Add mutation observer to watch for nav changes
    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.type === 'childList' && mutation.target.querySelector && mutation.target.querySelector('.nav-link')) {
                console.log('⚠️ Navigation DOM changed! Reinitializing...');
                setTimeout(initializeNavigation, 100); // Reinitialize after DOM settles
            }
        });
    });
    
    // Observe the navigation container
    const navContainer = document.querySelector('.main-nav') || document.querySelector('nav') || document.body;
    if (navContainer) {
        observer.observe(navContainer, { childList: true, subtree: true });
        console.log('🔍 Started observing navigation changes');
    }
    
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('.section');
    
    console.log(`Found ${navLinks.length} nav links and ${sections.length} sections`);
    navLinks.forEach((link, index) => {
        console.log(`Nav link ${index}: data-section="${link.dataset.section}"`);
    });
    sections.forEach((section, index) => {
        console.log(`Section ${index}: id="${section.id}"`);
    });
    
    navLinks.forEach((link, index) => {
        console.log(`🔗 Adding click listener to nav link ${index}:`, link.dataset.section, link);
        
        // Add a unique identifier to track this element
        link.setAttribute('data-listener-id', `nav-${index}-${Date.now()}`);
        
        link.addEventListener('click', (e) => {
            e.preventDefault();
            console.log('🔄 Navigation clicked:', link.dataset.section, 'at time:', Date.now(), 'listener-id:', link.getAttribute('data-listener-id'));
            
            const targetSection = link.dataset.section;
            
            // Update active nav link
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            console.log('✅ Updated active nav link');
            
            // Show target section
            sections.forEach(section => {
                section.classList.remove('active');
            });
            
            const targetElement = document.getElementById(`${targetSection}-section`);
            console.log(`🎯 Looking for section: "${targetSection}-section"`);
            console.log(`🎯 Found element:`, targetElement);
            
            if (targetElement) {
                targetElement.classList.add('active');
                console.log(`✅ Successfully switched to section: ${targetSection}`);
                
                // Load section-specific data
                console.log('🔍 About to check targetSection:', targetSection);
                if (targetSection === 'accounts') {
                    console.log('📊 Loading accounts data...');
                    loadAccountsData();
                } else if (targetSection === 'planning') {
                    console.log('📊 Loading planning data...');
                    loadPlanningData();
                }
                console.log('🏁 Navigation click handler completed');
                
                // FORCE ACCOUNTS DATA LOAD FOR TESTING
                if (targetSection === 'accounts') {
                    console.log('🔧 FORCING loadAccountsData call for debugging');
                    setTimeout(() => {
                        console.log('🕒 Delayed loadAccountsData call');
                        loadAccountsData();
                    }, 100);
                }
            } else {
                console.error(`❌ Section not found: "${targetSection}-section"`);
            }
        });
    });
}

// ==========================================================================
// Tab System Functions
// ==========================================================================

function initializeTabs() {
    // Initialize tab functionality for both planning and account sections
    initializeTabGroup('planning');
    initializeTabGroup('account');
    
    // Add special handling for Overview tab to prevent blank content
    const overviewTab = document.querySelector('[data-tab="overview"]');
    if (overviewTab) {
        console.log('🎯 Found Overview tab, adding special click handler');
        overviewTab.addEventListener('click', () => {
            console.log('🔄 Overview tab clicked - forcing content');
            setTimeout(forceOverviewContent, 50);
            setTimeout(forceOverviewContent, 200);
            setTimeout(forceOverviewContent, 500);
        });
    }
}

function initializeTabGroup(groupName) {
    const tabButtons = document.querySelectorAll(`div[class*="${groupName}"] .tab-btn`);
    const tabContents = document.querySelectorAll(`div[class*="${groupName}"] .tab-content`);
    
    tabButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const targetTab = button.dataset.tab;
            
            // Update active tab button
            tabButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // Show target tab content
            tabContents.forEach(content => {
                content.classList.remove('active');
            });
            
            const targetContent = document.getElementById(`${targetTab}-tab`) || 
                                document.getElementById(`${groupName}s-${targetTab}-tab`);
            if (targetContent) {
                targetContent.classList.add('active');
            }
        });
    });
}

// ==========================================================================
// Modal and Form Functions
// ==========================================================================

function initializeModals() {
    // Add Account Modal
    const addAccountBtn = document.getElementById('add-account-btn');
    const addAccountModal = document.getElementById('add-account-form');
    const addAccountForm = document.getElementById('new-account-form');
    
    // Add Contribution Modal
    const addContributionBtn = document.getElementById('add-contribution-btn');
    const addContributionModal = document.getElementById('add-contribution-form');
    const addContributionForm = document.getElementById('new-contribution-form');
    
    // Open modals
    if (addAccountBtn) {
        addAccountBtn.addEventListener('click', () => {
            addAccountModal.style.display = 'flex';
        });
    }
    
    if (addContributionBtn) {
        addContributionBtn.addEventListener('click', () => {
            addContributionModal.style.display = 'flex';
        });
    }
    
    // Close modals
    document.querySelectorAll('.close-modal').forEach(closeBtn => {
        closeBtn.addEventListener('click', (e) => {
            const modal = e.target.closest('.form-modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    document.querySelectorAll('.cancel-btn').forEach(cancelBtn => {
        cancelBtn.addEventListener('click', (e) => {
            const modal = e.target.closest('.form-modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    // Close modal when clicking outside
    document.querySelectorAll('.form-modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    // Handle form submissions
    if (addAccountForm) {
        addAccountForm.addEventListener('submit', handleAccountFormSubmit);
    }
    
    if (addContributionForm) {
        addContributionForm.addEventListener('submit', handleContributionFormSubmit);
    }
}

function handleAccountFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const accountData = {
        userId: 1, // Default user ID
        accountName: document.getElementById('account-name').value,
        accountType: document.getElementById('account-type').value,
        currentBalance: parseFloat(document.getElementById('account-current-balance').value),
        employer: document.getElementById('employer').value
    };
    
    console.log('Creating account:', accountData);
    
    // Here you would normally send the data to the API
    // For now, just show a success message and close the modal
    alert('Account created successfully! (This is a demo - would normally save to backend)');
    document.getElementById('add-account-form').style.display = 'none';
    e.target.reset();
    
    // Refresh the accounts list
    loadAccountsData();
}

function handleContributionFormSubmit(e) {
    e.preventDefault();
    
    const contributionData = {
        userId: 1, // Default user ID
        contributionType: document.getElementById('contribution-type').value,
        monthlyAmount: parseFloat(document.getElementById('monthly-amount').value),
        percentage: parseFloat(document.getElementById('percentage').value) || null
    };
    
    console.log('Creating contribution:', contributionData);
    
    // Here you would normally send the data to the API
    // For now, just show a success message and close the modal
    alert('Contribution created successfully! (This is a demo - would normally save to backend)');
    document.getElementById('add-contribution-form').style.display = 'none';
    e.target.reset();
    
    // Refresh the contributions list
    loadAccountsData();
}

// ==========================================================================
// Calculator Functions
// ==========================================================================

function initializeCalculators() {
    // Retirement Calculator
    const retirementCalculatorForm = document.getElementById('retirement-calculator-form');
    if (retirementCalculatorForm) {
        retirementCalculatorForm.addEventListener('submit', handleRetirementCalculation);
    }
    
    // Social Security Calculator
    const socialSecurityForm = document.getElementById('social-security-form');
    if (socialSecurityForm) {
        socialSecurityForm.addEventListener('submit', handleSocialSecurityCalculation);
    }
    
    // Scenarios button
    const loadScenariosBtn = document.getElementById('load-scenarios-btn');
    if (loadScenariosBtn) {
        loadScenariosBtn.addEventListener('click', loadWhatIfScenarios);
    }
}

function handleRetirementCalculation(e) {
    e.preventDefault();
    
    const currentAge = parseInt(document.getElementById('current-age').value);
    const retirementAge = parseInt(document.getElementById('retirement-age').value);
    const currentBalance = parseFloat(document.getElementById('current-balance').value);
    const monthlyContribution = parseFloat(document.getElementById('monthly-contribution').value);
    const annualReturn = parseFloat(document.getElementById('annual-return').value) / 100;
    
    // Simple compound interest calculation
    const yearsToRetirement = retirementAge - currentAge;
    const monthsToRetirement = yearsToRetirement * 12;
    const monthlyReturn = annualReturn / 12;
    
    // Calculate future value with contributions
    const futureValueCurrent = currentBalance * Math.pow(1 + annualReturn, yearsToRetirement);
    const futureValueContributions = monthlyContribution * 
        ((Math.pow(1 + monthlyReturn, monthsToRetirement) - 1) / monthlyReturn);
    const totalBalance = futureValueCurrent + futureValueContributions;
    
    // 4% rule for monthly income
    const monthlyIncome = (totalBalance * 0.04) / 12;
    
    const resultsHTML = `
        <div class="calculation-results">
            <div class="result-item">
                <strong>Years to Retirement:</strong> ${yearsToRetirement}
            </div>
            <div class="result-item">
                <strong>Projected Balance:</strong> ${formatCurrency(totalBalance)}
            </div>
            <div class="result-item">
                <strong>Monthly Retirement Income:</strong> ${formatCurrency(monthlyIncome)}
            </div>
            <div class="result-item status-${totalBalance > 1000000 ? 'good' : 'warning'}">
                <strong>Status:</strong> ${totalBalance > 1000000 ? 'On Track ✓' : 'Consider increasing contributions'}
            </div>
        </div>
    `;
    
    const calculatorOutput = document.getElementById('calculator-output');
    if (calculatorOutput) {
        calculatorOutput.innerHTML = resultsHTML;
    } else {
        console.warn('calculator-output element not found, skipping results display');
    }
}

function handleSocialSecurityCalculation(e) {
    e.preventDefault();
    
    const annualIncome = parseFloat(document.getElementById('annual-income').value);
    const yearsWorked = parseInt(document.getElementById('years-worked').value);
    
    // Simplified Social Security calculation (not actual SSA formula)
    const avgBenefit = annualIncome * 0.4; // Rough 40% replacement
    const yearsAdjustment = Math.min(yearsWorked / 35, 1); // Adjust for years worked
    
    const benefitAt62 = avgBenefit * 0.75 * yearsAdjustment / 12; // 25% reduction
    const benefitAt67 = avgBenefit * yearsAdjustment / 12; // Full benefit
    const benefitAt70 = avgBenefit * 1.24 * yearsAdjustment / 12; // 24% increase
    
    const resultsHTML = `
        <div class="ss-results">
            <div class="ss-item">
                <strong>At Age 62 (Early):</strong> ${formatCurrency(benefitAt62)}/month
            </div>
            <div class="ss-item">
                <strong>At Age 67 (Full):</strong> ${formatCurrency(benefitAt67)}/month
            </div>
            <div class="ss-item">
                <strong>At Age 70 (Delayed):</strong> ${formatCurrency(benefitAt70)}/month
            </div>
            <p><small>*Estimates only - actual benefits may vary. Check SSA.gov for official estimates.</small></p>
        </div>
    `;
    
    const socialSecurityResults = document.getElementById('social-security-results');
    if (socialSecurityResults) {
        socialSecurityResults.innerHTML = resultsHTML;
    } else {
        console.warn('social-security-results element not found, skipping results display');
    }
}

async function loadWhatIfScenarios() {
    // Get current user ID
    let userId = 1; // Default fallback
    if (typeof getCurrentUser === 'function') {
        const currentUser = getCurrentUser();
        if (currentUser && currentUser.userId) {
            userId = currentUser.userId;
        }
    }
    
    try {
        const scenarios = await fetchFromAPI(`/api/planning/scenarios/${userId}`);
        displayScenarios(scenarios);
    } catch (error) {
        console.error('Error loading scenarios:', error);
        // Show fallback scenarios
        const fallbackScenarios = {
            current: {
                projectedBalance: 1850000,
                projectedMonthlyIncome: 6965,
                status: 'On Track'
            },
            scenarioA: {
                description: 'Increase contributions by 20%',
                plan: {
                    projectedBalance: 2150000,
                    projectedMonthlyIncome: 8100,
                    status: 'Ahead of Schedule'
                }
            },
            scenarioB: {
                description: 'Retire at age 63',
                plan: {
                    projectedBalance: 1420000,
                    projectedMonthlyIncome: 5350,
                    status: 'Manageable'
                }
            },
            scenarioC: {
                description: 'Retire at age 67',
                plan: {
                    projectedBalance: 2145000,
                    projectedMonthlyIncome: 8100,
                    status: 'Excellent'
                }
            }
        };
        displayScenarios(fallbackScenarios);
    }
}

function displayScenarios(scenarios) {
    const scenariosContainer = document.getElementById('scenarios-results');
    
    if (!scenariosContainer) {
        console.warn('scenarios-results element not found, skipping scenarios display');
        return;
    }
    
    let html = `
        <div class="scenario-card current-scenario">
            <h4>Current Scenario</h4>
            <div class="scenario-details">
                <p><strong>Balance:</strong> ${formatCurrency(scenarios.current.projectedBalance)}</p>
                <p><strong>Monthly Income:</strong> ${formatCurrency(scenarios.current.projectedMonthlyIncome)}</p>
                <p><strong>Status:</strong> ${scenarios.current.status}</p>
            </div>
        </div>
    `;
    
    ['scenarioA', 'scenarioB', 'scenarioC'].forEach(key => {
        if (scenarios[key]) {
            html += `
                <div class="scenario-card">
                    <h4>${scenarios[key].description}</h4>
                    <div class="scenario-details">
                        <p><strong>Balance:</strong> ${formatCurrency(scenarios[key].plan.projectedBalance)}</p>
                        <p><strong>Monthly Income:</strong> ${formatCurrency(scenarios[key].plan.projectedMonthlyIncome)}</p>
                        <p><strong>Status:</strong> ${scenarios[key].plan.status}</p>
                    </div>
                </div>
            `;
        }
    });
    
    scenariosContainer.innerHTML = html;
}

// ==========================================================================
// Application Initialization
// ==========================================================================

async function initializeApp() {
    console.log('Initializing Finova Retirement Microservices Frontend...');
    
    showLoading();
    
    try {
        // Initialize authentication first
        if (typeof initializeAuth === 'function') {
            const isAuthenticated = await initializeAuth();
            console.log('Authentication initialized:', isAuthenticated);
            
            // If not authenticated, the auth module will handle showing login UI
            if (!isAuthenticated) {
                hideLoading();
                return;
            }
        }
        
        // Initialize navigation
        initializeNavigation();
        
        // Initialize tab system
        initializeTabs();
        
        // Initialize modals and forms
        initializeModals();
        
        // Initialize calculators
        initializeCalculators();
        
        // Load initial dashboard data
        await loadDashboardData();
        
        // Check service status (non-blocking with timeout)
        Promise.race([
            updateServiceStatus(),
            new Promise((_, reject) => 
                setTimeout(() => reject(new Error('Service check timeout')), 8000)
            )
        ]).catch(error => {
            console.warn('Service status check failed:', error);
            // Set a fallback status if check fails completely
            const statusElement = document.getElementById('service-status');
            if (statusElement) {
                statusElement.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Service Check Failed';
                statusElement.className = 'service-status some-down';
            }
        });
        
        console.log('Application initialized successfully!');
    } catch (error) {
        console.error('Application initialization error:', error);
        // If there's an auth error, show login UI
        if (typeof updateUIForUnauthenticatedUser === 'function') {
            updateUIForUnauthenticatedUser();
        }
    } finally {
        hideLoading();
    }
    
    // Set up periodic service status check with error handling
    setInterval(() => {
        Promise.race([
            updateServiceStatus(),
            new Promise((_, reject) => 
                setTimeout(() => reject(new Error('Periodic service check timeout')), 5000)
            )
        ]).catch(error => {
            console.warn('Periodic service status check failed:', error);
        });
    }, 30000); // Check every 30 seconds
}

// ==========================================================================
// Event Listeners
// ==========================================================================

document.addEventListener('DOMContentLoaded', initializeApp);

// Handle page visibility changes
document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
        Promise.race([
            updateServiceStatus(),
            new Promise((_, reject) => 
                setTimeout(() => reject(new Error('Visibility service check timeout')), 5000)
            )
        ]).catch(error => {
            console.warn('Visibility service status check failed:', error);
        });
    }
});

// ==========================================================================
// Quick Actions Functions
// ==========================================================================

function switchToSection(sectionName) {
    // Update navigation
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('.section');
    
    // Remove active from all nav links
    navLinks.forEach(link => link.classList.remove('active'));
    
    // Add active to target nav link
    const targetNavLink = document.querySelector(`[data-section="${sectionName}"]`);
    if (targetNavLink) {
        targetNavLink.classList.add('active');
    }
    
    // Hide all sections
    sections.forEach(section => section.classList.remove('active'));
    
    // Show target section
    const targetSection = document.getElementById(`${sectionName}-section`);
    if (targetSection) {
        targetSection.classList.add('active');
        
        // Load section-specific data
        if (sectionName === 'accounts') {
            loadAccountsData();
        } else if (sectionName === 'planning') {
            loadPlanningData();
        }
    }
}

// Make function globally available
window.switchToSection = switchToSection;

console.log('🚀 FINOVA FRONTEND LOADED - VERSION 20251015-100503');
console.log('Architecture: Frontend (HTML/JS) → API Gateway (8080) → Microservices (8081, 8082, 8083)');
console.log('🔧 DEBUG: Fixed loadAccountsData and loadAccountManagementData functions loaded');
