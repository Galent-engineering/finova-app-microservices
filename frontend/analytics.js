// Analytics Dashboard JavaScript

const ANALYTICS_SERVICE_URL = 'http://localhost:8085';

let dashboardData = null;
let savingsGrowthChart = null;
let contributionChart = null;
let accountChart = null;
let currentPeriod = '12m';

// Get user ID from auth or default to 1
function getUserId() {
    if (typeof getCurrentUser === 'function') {
        const user = getCurrentUser();
        if (user) {
            // Try different user ID properties
            if (user.userId) return user.userId;
            if (user.id) return user.id;
            if (user.sub) return parseInt(user.sub) || 1;
        }
    }
    // Default to user ID 1 - works for demo data
    console.log('Using default user ID: 1');
    return 1;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    setupTimeSelector();
    loadAnalyticsDashboard(currentPeriod);
});

// Setup time period selector
function setupTimeSelector() {
    const buttons = document.querySelectorAll('.time-button');
    buttons.forEach(button => {
        button.addEventListener('click', () => {
            buttons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            currentPeriod = button.dataset.period;
            loadAnalyticsDashboard(currentPeriod);
        });
    });
}

// Load analytics dashboard data
async function loadAnalyticsDashboard(period) {
    try {
        const userId = getUserId();
        const response = await fetch(`${ANALYTICS_SERVICE_URL}/api/analytics/dashboard/${userId}?period=${period}`);
        
        if (response.ok) {
            dashboardData = await response.json();
            displayDashboard(dashboardData);
        } else {
            throw new Error('Failed to load analytics dashboard');
        }
    } catch (error) {
        console.error('Error loading analytics:', error);
        displayError();
    }
}

// Display all dashboard components
function displayDashboard(data) {
    displayKeyStats(data.keyStats);
    displaySavingsGrowthChart(data.savingsGrowth);
    displayContributionChart(data.contributionBreakdown);
    displayAccountChart(data.accountBreakdown);
    displayQuarterlyReturns(data.quarterlyReturns);
    displayInsights(data.insights);
}

// Display key statistics cards
function displayKeyStats(stats) {
    const container = document.getElementById('stats-grid');
    
    container.innerHTML = `
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-label">Total Assets</span>
                <span class="stat-trend trend-${stats.totalAssetsTrend}">
                    <i class="fas fa-arrow-${stats.totalAssetsTrend === 'up' ? 'up' : 'down'}"></i>
                    ${stats.trendPercentage.toFixed(1)}%
                </span>
            </div>
            <div class="stat-value">$${formatNumber(stats.totalAssets)}</div>
            <div class="stat-subtitle">Total retirement savings</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-label">Annual Contribution</span>
                <span class="stat-trend trend-${stats.annualContributionTrend}">
                    <i class="fas fa-arrow-${stats.annualContributionTrend === 'up' ? 'up' : 'down'}"></i>
                    ${stats.contributionTrendPercentage.toFixed(1)}%
                </span>
            </div>
            <div class="stat-value">$${formatNumber(stats.annualContribution)}</div>
            <div class="stat-subtitle">Combined contributions YTD</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-label">YTD Return</span>
                <span class="stat-trend trend-${stats.ytdReturnTrend}">
                    <i class="fas fa-arrow-${stats.ytdReturnTrend === 'up' ? 'up' : 'down'}"></i>
                    ${stats.returnTrendPercentage.toFixed(1)}%
                </span>
            </div>
            <div class="stat-value">${stats.ytdReturn.toFixed(1)}%</div>
            <div class="stat-subtitle">Year-to-date performance</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-label">On Track Score</span>
            </div>
            <div class="stat-value">${stats.onTrackScore}/100</div>
            <div class="stat-subtitle">${stats.onTrackStatus}</div>
        </div>
    `;
}

// Display savings growth line chart
function displaySavingsGrowthChart(data) {
    const ctx = document.getElementById('savingsGrowthChart').getContext('2d');
    
    // Destroy existing chart if it exists
    if (savingsGrowthChart) {
        savingsGrowthChart.destroy();
    }
    
    savingsGrowthChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map(d => d.month),
            datasets: [
                {
                    label: 'Actual Balance',
                    data: data.map(d => d.actualBalance),
                    borderColor: '#4f46e5',
                    backgroundColor: 'rgba(79, 70, 229, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Target Balance',
                    data: data.map(d => d.targetBalance),
                    borderColor: '#94a3b8',
                    backgroundColor: 'transparent',
                    borderWidth: 2,
                    borderDash: [5, 5],
                    fill: false,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': $' + formatNumber(context.parsed.y);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        callback: function(value) {
                            return '$' + formatNumber(value);
                        }
                    }
                }
            }
        }
    });
}

// Display contribution breakdown donut chart
function displayContributionChart(data) {
    const ctx = document.getElementById('contributionChart').getContext('2d');
    
    // Destroy existing chart if it exists
    if (contributionChart) {
        contributionChart.destroy();
    }
    
    contributionChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Employee Contributions', 'Employer Match', 'Previous Balance'],
            datasets: [{
                data: [
                    data.employeeContributions,
                    data.employerMatch,
                    data.previousBalance
                ],
                backgroundColor: [
                    '#4f46e5',
                    '#06b6d4',
                    '#10b981'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = '$' + formatNumber(context.parsed);
                            const percentage = ((context.parsed / data.total) * 100).toFixed(1);
                            return label + ': ' + value + ' (' + percentage + '%)';
                        }
                    }
                }
            }
        }
    });
}

// Display account breakdown bar chart
function displayAccountChart(accounts) {
    const ctx = document.getElementById('accountChart').getContext('2d');
    
    // Destroy existing chart if it exists
    if (accountChart) {
        accountChart.destroy();
    }
    
    accountChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: accounts.map(a => a.accountName),
            datasets: [{
                label: 'Balance',
                data: accounts.map(a => a.balance),
                backgroundColor: accounts.map(a => a.color),
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const account = accounts[context.dataIndex];
                            return '$' + formatNumber(context.parsed.y) + ' (' + account.percentage.toFixed(1) + '%)';
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + formatNumber(value);
                        }
                    }
                }
            }
        }
    });
}

// Display quarterly returns
function displayQuarterlyReturns(returns) {
    const container = document.getElementById('quarterly-returns');
    
    container.innerHTML = `
        <div class="quarter-card">
            <div class="quarter-label">Q1</div>
            <div class="quarter-value">${returns.q1Return.toFixed(1)}%</div>
        </div>
        <div class="quarter-card">
            <div class="quarter-label">Q2</div>
            <div class="quarter-value">${returns.q2Return.toFixed(1)}%</div>
        </div>
        <div class="quarter-card">
            <div class="quarter-label">Q3</div>
            <div class="quarter-value">${returns.q3Return.toFixed(1)}%</div>
        </div>
        <div class="quarter-card">
            <div class="quarter-label">Q4</div>
            <div class="quarter-value">${returns.q4Return.toFixed(1)}%</div>
        </div>
        <div class="quarter-card" style="background: #4f46e5; color: white;">
            <div class="quarter-label" style="color: rgba(255,255,255,0.8);">YTD Average</div>
            <div class="quarter-value" style="color: white;">${returns.ytdAverage.toFixed(1)}%</div>
        </div>
    `;
}

// Display insights
function displayInsights(insights) {
    const container = document.getElementById('insights-grid');
    
    container.innerHTML = insights.map(insight => `
        <div class="insight-card ${insight.type}">
            <div class="insight-icon">
                <i class="fas fa-${insight.icon}"></i>
            </div>
            <div class="insight-title">${insight.title}</div>
            <div class="insight-description">${insight.description}</div>
        </div>
    `).join('');
}

// Display error
function displayError() {
    document.getElementById('stats-grid').innerHTML = `
        <div class="loading" style="color: #ef4444;">
            <i class="fas fa-exclamation-triangle"></i>
            <p>Failed to load analytics data. Please try again later.</p>
        </div>
    `;
}

// Utility function to format large numbers
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toFixed(0);
}

