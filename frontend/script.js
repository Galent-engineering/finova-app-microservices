// ==========================================================================
// Finova Retirement - Microservices Frontend JavaScript
// This frontend connects to the microservices via API Gateway
// ==========================================================================

// Configuration - API Gateway Base URL
const API_BASE_URL = 'http://localhost:8080';
const SERVICES = {
    USER: 'http://localhost:8081',
    ACCOUNT: 'http://localhost:8082', 
    PLANNING: 'http://localhost:8083'
};

// Application State
let servicesStatus = {
    user: false,
    account: false,
    planning: false
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
        const response = await fetch(`${url}/api/${serviceName}/health`, {
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
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            signal: AbortSignal.timeout(10000)
        });
        
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

async function updateServiceStatus() {
    console.log('Checking all service statuses...');
    
    // Check services both via API Gateway and directly
    const userStatus = await checkMultipleHealthEndpoints('users', [
        `${API_BASE_URL}/api/users/health`,
        `${SERVICES.USER}/api/users/health`
    ]);
    
    const accountStatus = await checkMultipleHealthEndpoints('accounts', [
        `${API_BASE_URL}/api/accounts/health`,
        `${SERVICES.ACCOUNT}/api/accounts/health`
    ]);
    
    const planningStatus = await checkMultipleHealthEndpoints('planning', [
        `${API_BASE_URL}/api/planning/health`,
        `${SERVICES.PLANNING}/api/planning/health`
    ]);
    
    // Update service cards
    updateServiceCard('user-service-card', userStatus);
    updateServiceCard('account-service-card', accountStatus);
    updateServiceCard('planning-service-card', planningStatus);
    
    // Update global status
    servicesStatus = {
        user: userStatus.status === 'up',
        account: accountStatus.status === 'up',
        planning: planningStatus.status === 'up'
    };
    
    updateGlobalServiceStatus();
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
                signal: AbortSignal.timeout(3000) // Shorter timeout for faster fallback
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
    if (!card) return;
    
    const statusDot = card.querySelector('.status-dot');
    const statusText = card.querySelector('.status-text');
    
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
    
    // Try to load data from Account Service (dashboard endpoint)
    try {
        const dashboardData = await fetchFromAPI('/api/dashboard/1');
        console.log('Dashboard data:', dashboardData);
        
        if (dashboardData.primaryAccount) {
            document.getElementById('monthly-income').textContent = 
                formatCurrency(dashboardData.primaryAccount.estimatedMonthlyIncome);
            document.getElementById('current-balance').textContent = 
                formatCurrency(dashboardData.primaryAccount.currentBalance);
        }
        
        document.getElementById('retirement-status').textContent = dashboardData.status || 'Status unavailable';
        
    } catch (error) {
        console.error('Dashboard data error:', error);
        // Fall back to sample data
        document.getElementById('monthly-income').textContent = '$6,965';
        document.getElementById('current-balance').textContent = '$106,965.67';
        document.getElementById('retirement-status').textContent = 'You are on track! ✓';
    }
}

async function loadAccountsData() {
    console.log('Loading accounts data...');
    
    try {
        // Try API Gateway first, then fall back to direct service
        let accounts;
        try {
            accounts = await fetchFromAPI('/api/accounts/user/1');
        } catch (error) {
            console.log('API Gateway failed, trying direct connection...');
            accounts = await fetch('http://localhost:8082/api/accounts/user/1').then(r => r.json());
        }
        
        // Display accounts
        if (accounts && accounts.length > 0) {
            let accountsHtml = '';
            accounts.forEach(account => {
                accountsHtml += `
                    <div class="account-item">
                        <strong>${account.accountName || account.accountType}</strong><br>
                        Balance: ${formatCurrency(account.currentBalance)}<br>
                        Status: ${account.onTrack ? 'On Track' : 'Behind Schedule'}
                    </div>
                `;
            });
            showSuccess('retirement-accounts', accountsHtml);
        } else {
            showSuccess('retirement-accounts', `
                <div class="account-item">
                    <strong>401(a) Plan</strong><br>
                    Balance: $106,965.67<br>
                    Status: On Track
                </div>
            `);
        }
        
        // Load contributions
        try {
            const contributionsSummary = await fetchFromAPI('/api/contributions/user/1/summary');
            showSuccess('contributions-summary', `
                <div class="contrib-item">
                    <strong>Total Monthly:</strong> ${contributionsSummary.totalMonthlyFormatted || '$975'}<br>
                    <strong>Total Annual:</strong> ${contributionsSummary.totalAnnualFormatted || '$11,700'}<br>
                    <small>Pre-tax, Roth, and Employer Match</small>
                </div>
            `);
        } catch (error) {
            console.log('Contributions via gateway failed, trying direct...');
            try {
                const contributionsSummary = await fetch('http://localhost:8082/api/contributions/user/1/summary').then(r => r.json());
                showSuccess('contributions-summary', `
                    <div class="contrib-item">
                        <strong>Total Monthly:</strong> ${contributionsSummary.totalMonthlyFormatted || '$975'}<br>
                        <strong>Total Annual:</strong> ${contributionsSummary.totalAnnualFormatted || '$11,700'}<br>
                        <small>Pre-tax, Roth, and Employer Match</small>
                    </div>
                `);
            } catch (directError) {
                showSuccess('contributions-summary', `
                    <div class="contrib-item">
                        <strong>Total Monthly:</strong> $975<br>
                        <strong>Total Annual:</strong> $11,700<br>
                        <small>Pre-tax, Roth, and Employer Match</small>
                    </div>
                `);
            }
        }
        
        // Load income sources
        try {
            const incomeSourcesSummary = await fetchFromAPI('/api/income-sources/user/1/summary');
            showSuccess('income-sources', `
                <div class="income-item">
                    <strong>401(k) - Finova:</strong> ${formatCurrency(incomeSourcesSummary.totalCurrentBalance || 106965)}<br>
                    <strong>Traditional IRA:</strong> $45,230<br>
                    <strong>Pension:</strong> Estimated
                </div>
            `);
        } catch (error) {
            console.log('Income sources via gateway failed, trying direct...');
            try {
                const incomeSourcesSummary = await fetch('http://localhost:8082/api/income-sources/user/1/summary').then(r => r.json());
                showSuccess('income-sources', `
                    <div class="income-item">
                        <strong>401(k) - Finova:</strong> ${formatCurrency(incomeSourcesSummary.totalCurrentBalance || 106965)}<br>
                        <strong>Traditional IRA:</strong> $45,230<br>
                        <strong>Pension:</strong> Estimated
                    </div>
                `);
            } catch (directError) {
                showSuccess('income-sources', `
                    <div class="income-item">
                        <strong>401(k) - Finova:</strong> $106,965<br>
                        <strong>Traditional IRA:</strong> $45,230<br>
                        <strong>Pension:</strong> Estimated
                    </div>
                `);
            }
        }
        
        // Load account management data
        loadAccountManagementData();
        
    } catch (error) {
        console.error('Accounts data error:', error);
        // Show fallback data
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
    }
}

// New function to load account management data
async function loadAccountManagementData() {
    try {
        // Load accounts list
        let accounts;
        try {
            accounts = await fetchFromAPI('/api/accounts/user/1');
        } catch (error) {
            accounts = await fetch('http://localhost:8082/api/accounts/user/1').then(r => r.json());
        }
        
        let accountsListHtml = '';
        if (accounts && accounts.length > 0) {
            accounts.forEach(account => {
                accountsListHtml += `
                    <div class="account-list-item">
                        <h4>${account.accountName || account.accountType}</h4>
                        <p>Balance: ${formatCurrency(account.currentBalance)}</p>
                        <p>Type: ${account.accountType}</p>
                        <p>Employer: ${account.employer || 'N/A'}</p>
                    </div>
                `;
            });
        } else {
            accountsListHtml = `
                <div class="account-list-item">
                    <h4>401(a) Plan - Finova</h4>
                    <p>Balance: $106,965.67</p>
                    <p>Type: 401K</p>
                    <p>Employer: Finova Corp</p>
                </div>
            `;
        }
        
        const accountsList = document.getElementById('accounts-list');
        if (accountsList) {
            accountsList.innerHTML = accountsListHtml;
        }
        
        // Load contributions list
        let contributions;
        try {
            contributions = await fetchFromAPI('/api/contributions/user/1');
        } catch (error) {
            contributions = await fetch('http://localhost:8082/api/contributions/user/1').then(r => r.json());
        }
        
        let contributionsListHtml = '';
        if (contributions && contributions.length > 0) {
            contributions.forEach(contrib => {
                contributionsListHtml += `
                    <div class="contribution-list-item">
                        <h4>${contrib.contributionType}</h4>
                        <p>Monthly: ${formatCurrency(contrib.monthlyAmount)}</p>
                        <p>Percentage: ${contrib.percentage || 'N/A'}%</p>
                    </div>
                `;
            });
        } else {
            contributionsListHtml = `
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
        
        const contributionsList = document.getElementById('contributions-list');
        if (contributionsList) {
            contributionsList.innerHTML = contributionsListHtml;
        }
        
    } catch (error) {
        console.error('Account management data error:', error);
        // Show fallback data
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
    }
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
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('.section');
    
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            const targetSection = link.dataset.section;
            
            // Update active nav link
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            
            // Show target section
            sections.forEach(section => {
                section.classList.remove('active');
            });
            
            const targetElement = document.getElementById(`${targetSection}-section`);
            if (targetElement) {
                targetElement.classList.add('active');
                
                // Load section-specific data
                if (targetSection === 'accounts') {
                    loadAccountsData();
                } else if (targetSection === 'planning') {
                    loadPlanningData();
                }
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
    
    document.getElementById('calculator-output').innerHTML = resultsHTML;
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
    
    document.getElementById('social-security-results').innerHTML = resultsHTML;
}

async function loadWhatIfScenarios() {
    try {
        const scenarios = await fetchFromAPI('/api/planning/scenarios/1');
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
        // Initialize navigation
        initializeNavigation();
        
        // Initialize tab system
        initializeTabs();
        
        // Initialize modals and forms
        initializeModals();
        
        // Initialize calculators
        initializeCalculators();
        
        // Check service status
        await updateServiceStatus();
        
        // Load initial dashboard data
        await loadDashboardData();
        
        console.log('Application initialized successfully!');
    } catch (error) {
        console.error('Application initialization error:', error);
    } finally {
        hideLoading();
    }
    
    // Set up periodic service status check
    setInterval(updateServiceStatus, 30000); // Check every 30 seconds
}

// ==========================================================================
// Event Listeners
// ==========================================================================

document.addEventListener('DOMContentLoaded', initializeApp);

// Handle page visibility changes
document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
        updateServiceStatus();
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

console.log('Finova Retirement Microservices Frontend Loaded! 🚀');
console.log('Architecture: Frontend (HTML/JS) → API Gateway (8080) → Microservices (8081, 8082, 8083)');
