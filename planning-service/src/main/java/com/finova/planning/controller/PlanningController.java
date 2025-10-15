package com.finova.planning.controller;

import com.finova.planning.dto.RetirementPlanDTO;
import com.finova.planning.dto.SocialSecurityDTO;
import com.finova.planning.dto.InvestmentStrategyDTO;
import com.finova.planning.dto.UserDTO;
import com.finova.planning.security.UserContext;
import com.finova.planning.service.RetirementCalculatorService;
import com.finova.planning.service.SocialSecurityService;
import com.finova.planning.service.InvestmentStrategyService;
import com.finova.planning.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Planning operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PlanningController {

    @Autowired
    private RetirementCalculatorService retirementCalculatorService;
    
    @Autowired
    private SocialSecurityService socialSecurityService;
    
    @Autowired
    private InvestmentStrategyService investmentStrategyService;
    
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Health check endpoint
     */
    @GetMapping("/planning/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Planning Service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    // ========== RETIREMENT PLANNING ENDPOINTS ==========
    
    /**
     * Get default retirement plan for user
     */
    @GetMapping("/planning/retirement-plan/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<RetirementPlanDTO> getRetirementPlan(@PathVariable Long userId) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        RetirementPlanDTO plan = new RetirementPlanDTO(userId, 42, 65);
        plan = retirementCalculatorService.calculateRetirementPlan(plan);
        return ResponseEntity.ok(plan);
    }
    
    /**
     * Calculate retirement plan with custom parameters
     */
    @PostMapping("/planning/retirement-plan/calculate")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#planDTO.userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<RetirementPlanDTO> calculateRetirementPlan(@Valid @RequestBody RetirementPlanDTO planDTO) {
        // Validate user access
        if (!authenticationService.validateUserAccess(planDTO.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        RetirementPlanDTO calculatedPlan = retirementCalculatorService.calculateRetirementPlan(planDTO);
        return ResponseEntity.ok(calculatedPlan);
    }
    
    /**
     * Get what-if scenarios for retirement planning
     */
    @GetMapping("/planning/scenarios/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<Map<String, Object>> getWhatIfScenarios(@PathVariable Long userId) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        Map<String, Object> scenarios = new HashMap<>();
        
        // Current scenario
        RetirementPlanDTO current = new RetirementPlanDTO(userId, 42, 65);
        current = retirementCalculatorService.calculateRetirementPlan(current);
        
        // Scenario A: Increase contributions by 20%
        RetirementPlanDTO scenarioA = new RetirementPlanDTO(userId, 42, 65);
        scenarioA.setMonthlyContribution(new BigDecimal("780"));
        scenarioA = retirementCalculatorService.calculateRetirementPlan(scenarioA);
        
        // Scenario B: Retire early at 63
        RetirementPlanDTO scenarioB = new RetirementPlanDTO(userId, 42, 63);
        scenarioB = retirementCalculatorService.calculateRetirementPlan(scenarioB);
        
        // Scenario C: Retire later at 67
        RetirementPlanDTO scenarioC = new RetirementPlanDTO(userId, 42, 67);
        scenarioC = retirementCalculatorService.calculateRetirementPlan(scenarioC);
        
        scenarios.put("current", current);
        scenarios.put("scenarioA", Map.of(
            "description", "Increase contributions by 20%",
            "plan", scenarioA
        ));
        scenarios.put("scenarioB", Map.of(
            "description", "Retire at age 63",
            "plan", scenarioB
        ));
        scenarios.put("scenarioC", Map.of(
            "description", "Retire at age 67",
            "plan", scenarioC
        ));
        
        return ResponseEntity.ok(scenarios);
    }
    
    // ========== SOCIAL SECURITY ENDPOINTS ==========
    
    /**
     * Get Social Security benefits estimate
     */
    @GetMapping("/planning/social-security/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<SocialSecurityDTO> getSocialSecurityBenefits(@PathVariable Long userId) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        SocialSecurityDTO ssDTO = new SocialSecurityDTO();
        ssDTO.setUserId(userId);
        ssDTO = socialSecurityService.calculateBenefits(ssDTO);
        return ResponseEntity.ok(ssDTO);
    }
    
    /**
     * Calculate Social Security benefits with custom parameters
     */
    @PostMapping("/planning/social-security/calculate")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#ssDTO.userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<SocialSecurityDTO> calculateSocialSecurityBenefits(@Valid @RequestBody SocialSecurityDTO ssDTO) {
        // Validate user access
        if (!authenticationService.validateUserAccess(ssDTO.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        
        SocialSecurityDTO calculatedSS = socialSecurityService.calculateBenefits(ssDTO);
        return ResponseEntity.ok(calculatedSS);
    }
    
    // ========== INVESTMENT STRATEGY ENDPOINTS ==========
    
    /**
     * Get investment strategy recommendations
     */
    @GetMapping("/planning/investment-strategy/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<InvestmentStrategyDTO> getInvestmentStrategy(@PathVariable Long userId, @RequestParam(required = false) Integer age) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        InvestmentStrategyDTO strategy = investmentStrategyService.generateStrategy(userId, age);
        return ResponseEntity.ok(strategy);
    }
    
    // ========== PLANNING TOOLS OVERVIEW ==========
    
    /**
     * Get planning tools overview
     */
    @GetMapping("/planning/tools/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<Map<String, Object>> getPlanningTools(@PathVariable Long userId) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        Map<String, Object> tools = new HashMap<>();
        
        tools.put("retirementPlanner", Map.of(
            "title", "Retirement Planner",
            "description", "Calculate your retirement savings goals",
            "endpoint", "/api/planning/retirement-plan/" + userId
        ));
        
        tools.put("socialSecurity", Map.of(
            "title", "Social Security Estimator",
            "description", "Estimate your Social Security benefits",
            "endpoint", "/api/planning/social-security/" + userId
        ));
        
        tools.put("investmentStrategy", Map.of(
            "title", "Investment Strategy",
            "description", "Get personalized investment recommendations",
            "endpoint", "/api/planning/investment-strategy/" + userId
        ));
        
        tools.put("scenarios", Map.of(
            "title", "What-If Scenarios",
            "description", "Compare different retirement scenarios",
            "endpoint", "/api/planning/scenarios/" + userId
        ));
        
        return ResponseEntity.ok(tools);
    }
    
    /**
     * Get comprehensive planning dashboard
     */
    @GetMapping("/planning/dashboard/{userId}")
    @PreAuthorize("isAuthenticated() and (@authenticationService.validateUserAccess(#userId) or hasRole('ADMIN') or hasRole('FINANCIAL_ADVISOR'))")
    public ResponseEntity<Map<String, Object>> getPlanningDashboard(@PathVariable Long userId) {
        // Validate user access
        if (!authenticationService.validateUserAccess(userId)) {
            return ResponseEntity.status(403).build();
        }
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get retirement plan
        RetirementPlanDTO retirementPlan = new RetirementPlanDTO(userId, 42, 65);
        retirementPlan = retirementCalculatorService.calculateRetirementPlan(retirementPlan);
        
        // Get Social Security
        SocialSecurityDTO socialSecurity = new SocialSecurityDTO();
        socialSecurity.setUserId(userId);
        socialSecurity = socialSecurityService.calculateBenefits(socialSecurity);
        
        // Get investment strategy
        InvestmentStrategyDTO investmentStrategy = investmentStrategyService.generateStrategy(userId, 42);
        
        dashboard.put("retirementPlan", retirementPlan);
        dashboard.put("socialSecurity", socialSecurity);
        dashboard.put("investmentStrategy", investmentStrategy);
        dashboard.put("summary", Map.of(
            "projectedRetirementIncome", retirementPlan.getFormattedProjectedMonthlyIncome(),
            "socialSecurityBenefit", socialSecurity.getFormattedBenefitAt67(),
            "portfolioValue", investmentStrategy.getFormattedPortfolioValue(),
            "overallStatus", retirementPlan.getStatus()
        ));
        
        return ResponseEntity.ok(dashboard);
    }
}
