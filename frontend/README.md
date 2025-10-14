# Finova Retirement - Microservices Frontend

A modern, responsive web frontend for the Finova Retirement Planning application, designed to work with a microservices architecture.

## Overview

This frontend application provides a comprehensive retirement planning dashboard that connects to multiple microservices:

- **User Service** (Port 8081) - User authentication and profiles
- **Account Service** (Port 8082) - Retirement accounts and contributions
- **Planning Service** (Port 8083) - Planning calculations and strategies
- **API Gateway** (Port 8080) - Single entry point for all services

## Features

### 🏠 Dashboard
- Real-time service status monitoring
- Monthly income projections
- Current balance overview
- Retirement readiness status
- Service health indicators

### 📊 Account Management
- **Overview Tab**: Summary of all retirement accounts
- **Manage Accounts Tab**: Add, edit, and delete retirement accounts
- **Manage Contributions Tab**: Configure contribution settings
- Support for multiple account types (401k, 403b, IRA, etc.)

### 🧮 Planning Tools
- **Overview Tab**: Current planning status
- **Retirement Calculator**: Interactive compound interest calculator
- **What-If Scenarios**: Compare different planning strategies
- **Social Security Calculator**: Estimate Social Security benefits

### ⚙️ Service Monitoring
- Microservices architecture overview
- API endpoint documentation
- Real-time health checks
- Service status indicators

## Technology Stack

- **Frontend**: Pure HTML5, CSS3, JavaScript (ES6+)
- **Icons**: Font Awesome 6.0
- **Architecture**: Single Page Application (SPA)
- **Communication**: REST APIs via Fetch API
- **Responsive**: Mobile-first design

## File Structure

```
frontend/
├── index.html          # Main application file
├── style.css          # Complete styling system
├── script.js          # Application logic
└── README.md          # This documentation
```

## Getting Started

### Prerequisites

- Modern web browser (Chrome, Firefox, Safari, Edge)
- Running microservices (User, Account, Planning services)
- API Gateway running on localhost:8080

### Installation

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd finova-retire-app-microsrv/frontend
   ```

2. **Start a local web server**:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js
   npx http-server
   
   # Using PHP
   php -S localhost:8000
   ```

3. **Open your browser**:
   ```
   http://localhost:8000
   ```

### Configuration

The frontend is configured to connect to services via the API Gateway at `http://localhost:8080`. To change this, update the `API_BASE_URL` constant in `script.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080';
```

## Features Detail

### Interactive Dashboard
- **Service Status Cards**: Real-time health monitoring of all microservices
- **Financial Overview**: Monthly income and current balance from Account Service
- **Planning Status**: Retirement readiness assessment from Planning Service

### Account Management System
- **Modal-based Forms**: Add new retirement accounts and contributions
- **Tabbed Interface**: Organized account information and management tools
- **Form Validation**: Client-side validation with user-friendly error messages

### Planning Tools
- **Retirement Calculator**: 
  - Compound interest calculations
  - Customizable parameters (age, contributions, returns)
  - Instant results with visual feedback
  
- **What-If Scenarios**:
  - Load scenarios from Planning Service
  - Compare different retirement strategies
  - Visual scenario comparison

- **Social Security Calculator**:
  - Estimate benefits at different claiming ages
  - Based on income and years worked
  - Educational disclaimers included

## API Integration

The frontend integrates with the following endpoints:

### Service Health Checks
- `GET /api/users/health`
- `GET /api/accounts/health`
- `GET /api/planning/health`

### Account Service
- `GET /api/dashboard/1` - Dashboard data
- `GET /api/accounts/user/1` - User accounts
- `GET /api/contributions/user/1/summary` - Contributions summary
- `GET /api/income-sources/user/1/summary` - Income sources

### Planning Service
- `GET /api/planning/retirement-plan/1` - Retirement plan
- `GET /api/planning/social-security/1` - Social Security estimates
- `GET /api/planning/investment-strategy/1` - Investment strategy
- `GET /api/planning/scenarios/1` - What-if scenarios

## Error Handling

The application includes comprehensive error handling:

- **Network Errors**: Graceful fallbacks when services are unavailable
- **Loading States**: Visual indicators during API calls
- **Service Status**: Clear indication of which services are online/offline
- **User Feedback**: Informative error messages and success notifications

## Responsive Design

The application is fully responsive with:

- **Mobile-first approach**: Optimized for mobile devices
- **Flexible layouts**: CSS Grid and Flexbox for adaptive designs
- **Touch-friendly**: Large buttons and touch targets
- **Tablet support**: Optimized for tablet viewing

## Browser Compatibility

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Testing

### Manual Testing Checklist

1. **Service Connection**:
   - [ ] All service status cards show correct status
   - [ ] Dashboard loads data from microservices
   - [ ] Error states show when services are down

2. **Navigation**:
   - [ ] All navigation links work correctly
   - [ ] Sections change properly
   - [ ] Active states are maintained

3. **Account Management**:
   - [ ] Tab switching works in accounts section
   - [ ] Add account modal opens and closes
   - [ ] Form validation works
   - [ ] Account creation demo works

4. **Planning Tools**:
   - [ ] Tab switching works in planning section
   - [ ] Retirement calculator produces results
   - [ ] Social Security calculator works
   - [ ] What-if scenarios load and display

5. **Responsive Design**:
   - [ ] Mobile layout works properly
   - [ ] Tablet layout is functional
   - [ ] Desktop layout is optimal

### Automated Testing

To add automated tests, consider:

```bash
# Install testing framework
npm install --save-dev jest jsdom

# Add test files
tests/
├── unit/
│   ├── calculator.test.js
│   └── api.test.js
└── integration/
    └── navigation.test.js
```

## Deployment

### Production Build

For production deployment:

1. **Minify CSS and JavaScript**:
   ```bash
   # Using uglify-js and clean-css
   npm install -g uglify-js clean-css-cli
   uglifyjs script.js -o script.min.js
   cleancss -o style.min.css style.css
   ```

2. **Update HTML references**:
   ```html
   <link rel="stylesheet" href="style.min.css">
   <script src="script.min.js"></script>
   ```

3. **Configure web server**:
   - Serve static files efficiently
   - Enable gzip compression
   - Set appropriate cache headers

### Environment Configuration

Create environment-specific configurations:

```javascript
// config/production.js
const config = {
    API_BASE_URL: 'https://api.finova.com',
    TIMEOUT: 10000,
    RETRY_ATTEMPTS: 3
};

// config/development.js
const config = {
    API_BASE_URL: 'http://localhost:8080',
    TIMEOUT: 5000,
    RETRY_ATTEMPTS: 1
};
```

## Contributing

1. Follow existing code style and conventions
2. Test changes thoroughly across browsers
3. Update documentation for new features
4. Ensure responsive design principles are maintained

## Troubleshooting

### Common Issues

1. **Services showing as offline**:
   - Verify all microservices are running
   - Check API Gateway is accessible
   - Review browser console for CORS errors

2. **Calculator not working**:
   - Check JavaScript console for errors
   - Verify form field names match JavaScript selectors
   - Ensure number inputs are properly validated

3. **Responsive issues**:
   - Test with browser developer tools
   - Verify CSS media queries are correct
   - Check for conflicting CSS rules

### Browser Console Commands

Debug the application using browser console:

```javascript
// Check service status
updateServiceStatus();

// Test API connectivity
fetchFromAPI('/api/accounts/health');

// Reload dashboard data
loadDashboardData();
```

## License

Copyright © 2024 Finova Financial. All rights reserved.

---

**Note**: This frontend is designed to work with the Finova Retirement microservices architecture. Ensure all backend services are properly configured and running for full functionality.
