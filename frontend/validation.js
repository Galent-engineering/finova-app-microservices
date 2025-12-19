// ==========================================================================
// Finova Retirement - Client-Side Validation Module
// Comprehensive form validation for all frontend forms
// ==========================================================================

/**
 * Validation rules and patterns
 */
const VALIDATION_RULES = {
    // Username validation
    username: {
        required: true,
        minLength: 3,
        maxLength: 50,
        pattern: /^[a-zA-Z0-9._-]+$/,
        message: "Username must be 3-50 characters and contain only letters, numbers, dots, underscores, and hyphens"
    },
    
    // Password validation
    password: {
        required: true,
        minLength: 8,
        maxLength: 128,
        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
        message: "Password must be 8-128 characters with at least one lowercase letter, one uppercase letter, one digit, and one special character"
    },
    
    // Name validation
    name: {
        required: true,
        minLength: 1,
        maxLength: 50,
        pattern: /^[a-zA-Z\s'-]+$/,
        message: "Name can only contain letters, spaces, apostrophes, and hyphens"
    },
    
    // Email validation
    email: {
        required: true,
        maxLength: 100,
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: "Please enter a valid email address"
    },
    
    // Phone validation
    phone: {
        required: false,
        pattern: /^\+?[1-9]\d{1,14}$/,
        message: "Please enter a valid phone number"
    },
    
    // Currency validation
    currency: {
        required: true,
        min: 0,
        max: 10000000,
        message: "Amount must be between $0 and $10,000,000"
    },
    
    // Percentage validation
    percentage: {
        required: true,
        min: 0,
        max: 100,
        message: "Percentage must be between 0% and 100%"
    },
    
    // Age validation
    age: {
        required: true,
        min: 18,
        max: 100,
        message: "Age must be between 18 and 100"
    },
    
    // Retirement age validation
    retirementAge: {
        required: true,
        min: 55,
        max: 75,
        message: "Retirement age must be between 55 and 75"
    },
    
    // Years validation
    years: {
        required: true,
        min: 0,
        max: 50,
        message: "Years must be between 0 and 50"
    }
};

/**
 * Validation utility functions
 */
class ValidationUtils {
    
    /**
     * Validate a single field based on rules
     */
    static validateField(value, rules, fieldName) {
        const errors = [];
        
        // Required validation
        if (rules.required && (!value || value.toString().trim() === '')) {
            errors.push(`${fieldName} is required`);
            return errors;
        }
        
        // Skip other validations if value is empty and not required
        if (!value || value.toString().trim() === '') {
            return errors;
        }
        
        // Length validation
        if (rules.minLength && value.length < rules.minLength) {
            errors.push(`${fieldName} must be at least ${rules.minLength} characters`);
        }
        
        if (rules.maxLength && value.length > rules.maxLength) {
            errors.push(`${fieldName} must not exceed ${rules.maxLength} characters`);
        }
        
        // Pattern validation
        if (rules.pattern && !rules.pattern.test(value)) {
            errors.push(rules.message || `${fieldName} format is invalid`);
        }
        
        // Numeric validation
        if (rules.min !== undefined && parseFloat(value) < rules.min) {
            errors.push(`${fieldName} must be at least ${rules.min}`);
        }
        
        if (rules.max !== undefined && parseFloat(value) > rules.max) {
            errors.push(`${fieldName} must not exceed ${rules.max}`);
        }
        
        return errors;
    }
    
    /**
     * Validate form data against rules
     */
    static validateForm(formData, rules) {
        const errors = {};
        let isValid = true;
        
        for (const [fieldName, fieldRules] of Object.entries(rules)) {
            const value = formData[fieldName];
            const fieldErrors = this.validateField(value, fieldRules, fieldName);
            
            if (fieldErrors.length > 0) {
                errors[fieldName] = fieldErrors;
                isValid = false;
            }
        }
        
        return { isValid, errors };
    }
    
    /**
     * Show validation errors in UI
     */
    static showFieldError(fieldId, errors) {
        const field = document.getElementById(fieldId);
        if (!field) return;
        
        // Remove existing error styling
        field.classList.remove('error');
        const existingError = field.parentNode.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
        
        if (errors && errors.length > 0) {
            // Add error styling
            field.classList.add('error');
            
            // Add error message
            const errorDiv = document.createElement('div');
            errorDiv.className = 'field-error';
            errorDiv.innerHTML = errors.join('<br>');
            field.parentNode.appendChild(errorDiv);
        }
    }
    
    /**
     * Clear field errors
     */
    static clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        if (!field) return;
        
        field.classList.remove('error');
        const existingError = field.parentNode.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
    }
    
    /**
     * Validate retirement age vs current age
     */
    static validateRetirementAge(currentAge, retirementAge) {
        const errors = [];
        
        if (currentAge && retirementAge) {
            if (parseInt(retirementAge) <= parseInt(currentAge)) {
                errors.push('Retirement age must be greater than current age');
            }
        }
        
        return errors;
    }
    
    /**
     * Validate investment allocation percentages
     */
    static validateAllocation(stocks, bonds, cash) {
        const errors = [];
        
        if (stocks !== undefined && bonds !== undefined && cash !== undefined) {
            const total = parseFloat(stocks || 0) + parseFloat(bonds || 0) + parseFloat(cash || 0);
            if (Math.abs(total - 100) > 0.01) { // Allow for small floating point differences
                errors.push(`Asset allocation percentages must sum to 100% (currently ${total.toFixed(1)}%)`);
            }
        }
        
        return errors;
    }
}

/**
 * Form-specific validation functions
 */
class FormValidators {
    
    /**
     * Validate registration form
     */
    static validateRegistration(formData) {
        const rules = {
            username: VALIDATION_RULES.username,
            password: VALIDATION_RULES.password,
            firstName: VALIDATION_RULES.name,
            lastName: VALIDATION_RULES.name,
            email: VALIDATION_RULES.email
        };
        
        return ValidationUtils.validateForm(formData, rules);
    }
    
    /**
     * Validate login form
     */
    static validateLogin(formData) {
        const rules = {
            username: VALIDATION_RULES.username,
            password: VALIDATION_RULES.password
        };
        
        return ValidationUtils.validateForm(formData, rules);
    }
    
    /**
     * Validate retirement account form
     */
    static validateRetirementAccount(formData) {
        const rules = {
            accountName: {
                required: true,
                minLength: 1,
                maxLength: 100,
                message: "Account name is required"
            },
            accountType: {
                required: true,
                message: "Account type is required"
            },
            currentBalance: {
                required: true,
                min: 0,
                max: 10000000,
                message: "Current balance must be between $0 and $10,000,000"
            }
        };
        
        return ValidationUtils.validateForm(formData, rules);
    }
    
    /**
     * Validate contribution form
     */
    static validateContribution(formData) {
        const rules = {
            contributionType: {
                required: true,
                message: "Contribution type is required"
            },
            monthlyAmount: {
                required: true,
                min: 0,
                max: 50000,
                message: "Monthly amount must be between $0 and $50,000"
            },
            percentage: {
                required: false,
                min: 0,
                max: 100,
                message: "Percentage must be between 0% and 100%"
            }
        };
        
        return ValidationUtils.validateForm(formData, rules);
    }
    
    /**
     * Validate retirement calculator form
     */
    static validateRetirementCalculator(formData) {
        const rules = {
            currentAge: VALIDATION_RULES.age,
            retirementAge: VALIDATION_RULES.retirementAge,
            currentBalance: {
                required: true,
                min: 0,
                max: 10000000,
                message: "Current savings must be between $0 and $10,000,000"
            },
            monthlyContribution: {
                required: true,
                min: 0,
                max: 50000,
                message: "Monthly contribution must be between $0 and $50,000"
            },
            annualReturn: {
                required: true,
                min: 1,
                max: 50,
                message: "Annual return must be between 1% and 50%"
            }
        };
        
        const result = ValidationUtils.validateForm(formData, rules);
        
        // Additional validation for retirement age vs current age
        if (formData.currentAge && formData.retirementAge) {
            const ageErrors = ValidationUtils.validateRetirementAge(formData.currentAge, formData.retirementAge);
            if (ageErrors.length > 0) {
                result.errors.retirementAge = ageErrors;
                result.isValid = false;
            }
        }
        
        return result;
    }
    
    /**
     * Validate social security form
     */
    static validateSocialSecurity(formData) {
        const rules = {
            annualIncome: {
                required: true,
                min: 0,
                max: 1000000,
                message: "Annual income must be between $0 and $1,000,000"
            },
            yearsWorked: VALIDATION_RULES.years
        };
        
        return ValidationUtils.validateForm(formData, rules);
    }
}

/**
 * Real-time validation for form fields
 */
class RealTimeValidator {
    
    /**
     * Add real-time validation to a field
     */
    static addFieldValidation(fieldId, rules, fieldName) {
        const field = document.getElementById(fieldId);
        if (!field) return;
        
        // Validate on blur
        field.addEventListener('blur', () => {
            const value = field.value;
            const errors = ValidationUtils.validateField(value, rules, fieldName);
            ValidationUtils.showFieldError(fieldId, errors);
        });
        
        // Clear errors on focus
        field.addEventListener('focus', () => {
            ValidationUtils.clearFieldError(fieldId);
        });
        
        // Validate on input for immediate feedback
        field.addEventListener('input', () => {
            const value = field.value;
            const errors = ValidationUtils.validateField(value, rules, fieldName);
            if (errors.length > 0) {
                ValidationUtils.showFieldError(fieldId, errors);
            } else {
                ValidationUtils.clearFieldError(fieldId);
            }
        });
    }
    
    /**
     * Initialize all form validations
     */
    static initializeAllValidations() {
        // Registration form validation
        this.addFieldValidation('username', VALIDATION_RULES.username, 'Username');
        this.addFieldValidation('password', VALIDATION_RULES.password, 'Password');
        this.addFieldValidation('firstName', VALIDATION_RULES.name, 'First Name');
        this.addFieldValidation('lastName', VALIDATION_RULES.name, 'Last Name');
        this.addFieldValidation('email', VALIDATION_RULES.email, 'Email');
        
        // Login form validation
        this.addFieldValidation('login-username', VALIDATION_RULES.username, 'Username');
        this.addFieldValidation('login-password', VALIDATION_RULES.password, 'Password');
        
        // Retirement account form validation
        this.addFieldValidation('account-name', {
            required: true,
            minLength: 1,
            maxLength: 100,
            message: "Account name is required"
        }, 'Account Name');
        
        this.addFieldValidation('account-current-balance', {
            required: true,
            min: 0,
            max: 10000000,
            message: "Current balance must be between $0 and $10,000,000"
        }, 'Current Balance');
        
        // Contribution form validation
        this.addFieldValidation('monthly-amount', {
            required: true,
            min: 0,
            max: 50000,
            message: "Monthly amount must be between $0 and $50,000"
        }, 'Monthly Amount');
        
        this.addFieldValidation('percentage', {
            required: false,
            min: 0,
            max: 100,
            message: "Percentage must be between 0% and 100%"
        }, 'Percentage');
        
        // Retirement calculator validation
        this.addFieldValidation('current-age', VALIDATION_RULES.age, 'Current Age');
        this.addFieldValidation('retirement-age', VALIDATION_RULES.retirementAge, 'Retirement Age');
        this.addFieldValidation('current-balance', {
            required: true,
            min: 0,
            max: 10000000,
            message: "Current savings must be between $0 and $10,000,000"
        }, 'Current Savings');
        
        this.addFieldValidation('monthly-contribution', {
            required: true,
            min: 0,
            max: 50000,
            message: "Monthly contribution must be between $0 and $50,000"
        }, 'Monthly Contribution');
        
        this.addFieldValidation('annual-return', {
            required: true,
            min: 1,
            max: 50,
            message: "Annual return must be between 1% and 50%"
        }, 'Annual Return');
        
        // Social security form validation
        this.addFieldValidation('annual-income', {
            required: true,
            min: 0,
            max: 1000000,
            message: "Annual income must be between $0 and $1,000,000"
        }, 'Annual Income');
        
        this.addFieldValidation('years-worked', VALIDATION_RULES.years, 'Years Worked');
    }
}

// Initialize validations when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    RealTimeValidator.initializeAllValidations();
});

// Export for use in other scripts
window.ValidationUtils = ValidationUtils;
window.FormValidators = FormValidators;
window.RealTimeValidator = RealTimeValidator;
