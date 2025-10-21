// Payment Dashboard JavaScript

const PAYMENT_SERVICE_URL = 'http://localhost:8084';

let currentSubscription = null;

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
    loadSubscription();
    loadPaymentHistory();
    
    // Set up form handlers
    document.getElementById('payment-method-form').addEventListener('submit', handlePaymentMethodUpdate);
    document.getElementById('frequency-form').addEventListener('submit', handleFrequencyUpdate);
});

// Load active subscription
async function loadSubscription() {
    try {
        const userId = getUserId();
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/subscriptions/${userId}/active`);
        
        if (response.ok) {
            currentSubscription = await response.json();
            displaySubscription(currentSubscription);
        } else if (response.status === 404) {
            displayNoSubscription();
        } else {
            throw new Error('Failed to load subscription');
        }
    } catch (error) {
        console.error('Error loading subscription:', error);
        displaySubscriptionError();
    }
}

// Display subscription details
function displaySubscription(subscription) {
    const card = document.getElementById('subscription-card');
    
    const nextPaymentDate = new Date(subscription.nextPaymentDate).toLocaleDateString('en-US', {
        month: 'long',
        day: 'numeric',
        year: 'numeric'
    });
    
    card.innerHTML = `
        <div class="subscription-header">
            <div class="subscription-info">
                <h2>${subscription.tier} Advisory Tier</h2>
                <span class="subscription-status status-${subscription.status.toLowerCase()}">${subscription.status}</span>
            </div>
        </div>
        
        <div class="subscription-details">
            <div class="detail-item">
                <span class="detail-label">Billing Frequency</span>
                <span class="detail-value">${subscription.frequency}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Amount</span>
                <span class="detail-value">$${subscription.amount.toFixed(2)}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Next Payment</span>
                <span class="detail-value">${nextPaymentDate}</span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Payment Method</span>
                <span class="detail-value">${subscription.paymentMethod.brand || 'Card'} ****${subscription.paymentMethod.last4}</span>
            </div>
        </div>
        
        <div class="quick-actions">
            <button class="action-button" onclick="openPaymentMethodModal()">
                <i class="fas fa-credit-card"></i> Change Payment Method
            </button>
            <button class="action-button" onclick="openFrequencyModal()">
                <i class="fas fa-calendar"></i> Update Frequency
            </button>
            <button class="action-button" onclick="openCancelModal()">
                <i class="fas fa-pause"></i> Pause/Cancel
            </button>
        </div>
    `;
}

// Display when no subscription exists
function displayNoSubscription() {
    const card = document.getElementById('subscription-card');
    card.innerHTML = `
        <div style="text-align: center; padding: 3rem;">
            <i class="fas fa-inbox" style="font-size: 3rem; color: #cbd5e1; margin-bottom: 1rem;"></i>
            <h3 style="color: #475569; margin-bottom: 0.5rem;">No Active Subscription</h3>
            <p style="color: #64748b; margin-bottom: 1.5rem;">You don't have an active advisory subscription yet.</p>
            <button class="btn btn-primary" onclick="window.location.href='index.html'">Explore Advisory Plans</button>
        </div>
    `;
}

// Display error
function displaySubscriptionError() {
    const card = document.getElementById('subscription-card');
    card.innerHTML = `
        <div style="text-align: center; padding: 2rem; color: #ef4444;">
            <i class="fas fa-exclamation-triangle" style="font-size: 2rem; margin-bottom: 1rem;"></i>
            <p>Failed to load subscription details. Please try again later.</p>
        </div>
    `;
}

// Load payment history
async function loadPaymentHistory() {
    try {
        const userId = getUserId();
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/payments/history/${userId}`);
        
        if (response.ok) {
            const history = await response.json();
            displayPaymentHistory(history);
        } else {
            throw new Error('Failed to load payment history');
        }
    } catch (error) {
        console.error('Error loading payment history:', error);
        displayPaymentHistoryError();
    }
}

// Display payment history
function displayPaymentHistory(payments) {
    const container = document.getElementById('payment-history');
    
    if (payments.length === 0) {
        container.innerHTML = `
            <div style="text-align: center; padding: 2rem; color: #64748b;">
                <i class="fas fa-receipt" style="font-size: 2rem; margin-bottom: 0.5rem;"></i>
                <p>No payment history yet</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = payments.map(payment => {
        const date = new Date(payment.processedAt || payment.createdAt).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
        
        const statusClass = payment.status === 'COMPLETED' ? 'completed' : 
                           payment.status === 'PENDING' ? 'pending' : 'failed';
        
        return `
            <div class="payment-item">
                <div>
                    <div class="payment-date">${date}</div>
                    <div style="font-size: 0.875rem; color: #64748b; margin-top: 0.25rem;">
                        ${payment.transactionId || 'Processing...'}
                    </div>
                </div>
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <div class="payment-amount">$${payment.amount.toFixed(2)}</div>
                    <span class="payment-status-badge status-${statusClass}">${payment.status}</span>
                </div>
            </div>
        `;
    }).join('');
}

// Display payment history error
function displayPaymentHistoryError() {
    const container = document.getElementById('payment-history');
    container.innerHTML = `
        <div style="text-align: center; padding: 2rem; color: #ef4444;">
            <p>Failed to load payment history</p>
        </div>
    `;
}

// Modal functions
function openPaymentMethodModal() {
    document.getElementById('payment-method-modal').classList.add('active');
}

function closePaymentMethodModal() {
    document.getElementById('payment-method-modal').classList.remove('active');
}

function openFrequencyModal() {
    if (currentSubscription) {
        document.getElementById('billing-frequency').value = currentSubscription.frequency;
    }
    document.getElementById('frequency-modal').classList.add('active');
}

function closeFrequencyModal() {
    document.getElementById('frequency-modal').classList.remove('active');
}

function openCancelModal() {
    document.getElementById('cancel-modal').classList.add('active');
}

function closeCancelModal() {
    document.getElementById('cancel-modal').classList.remove('active');
}

// Handle payment method update
async function handlePaymentMethodUpdate(e) {
    e.preventDefault();
    
    if (!currentSubscription) {
        alert('No active subscription found');
        return;
    }
    
    const paymentMethod = {
        type: document.getElementById('payment-type').value,
        holderName: document.getElementById('holder-name').value,
        last4: document.getElementById('last4').value,
        brand: document.getElementById('brand').value
    };
    
    try {
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/subscriptions/${currentSubscription.id}/payment-method`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(paymentMethod)
        });
        
        if (response.ok) {
            alert('Payment method updated successfully!');
            closePaymentMethodModal();
            loadSubscription();
        } else {
            throw new Error('Failed to update payment method');
        }
    } catch (error) {
        console.error('Error updating payment method:', error);
        alert('Failed to update payment method. Please try again.');
    }
}

// Handle frequency update
async function handleFrequencyUpdate(e) {
    e.preventDefault();
    
    if (!currentSubscription) {
        alert('No active subscription found');
        return;
    }
    
    const frequency = document.getElementById('billing-frequency').value;
    
    const updateData = {
        ...currentSubscription,
        frequency: frequency
    };
    
    try {
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/subscriptions/${currentSubscription.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData)
        });
        
        if (response.ok) {
            alert('Billing frequency updated successfully!');
            closeFrequencyModal();
            loadSubscription();
        } else {
            throw new Error('Failed to update frequency');
        }
    } catch (error) {
        console.error('Error updating frequency:', error);
        alert('Failed to update billing frequency. Please try again.');
    }
}

// Pause subscription
async function pauseSubscription() {
    if (!currentSubscription) {
        alert('No active subscription found');
        return;
    }
    
    if (!confirm('Are you sure you want to pause your subscription?')) {
        return;
    }
    
    try {
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/subscriptions/${currentSubscription.id}/pause`, {
            method: 'PATCH'
        });
        
        if (response.ok) {
            alert('Subscription paused successfully');
            closeCancelModal();
            loadSubscription();
        } else {
            throw new Error('Failed to pause subscription');
        }
    } catch (error) {
        console.error('Error pausing subscription:', error);
        alert('Failed to pause subscription. Please try again.');
    }
}

// Cancel subscription
async function cancelSubscription() {
    if (!currentSubscription) {
        alert('No active subscription found');
        return;
    }
    
    if (!confirm('Are you sure you want to cancel your subscription? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${PAYMENT_SERVICE_URL}/api/subscriptions/${currentSubscription.id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            alert('Subscription cancelled successfully');
            closeCancelModal();
            loadSubscription();
        } else {
            throw new Error('Failed to cancel subscription');
        }
    } catch (error) {
        console.error('Error cancelling subscription:', error);
        alert('Failed to cancel subscription. Please try again.');
    }
}

