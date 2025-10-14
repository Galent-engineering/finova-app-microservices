package com.finova.account.controller;

import com.finova.account.dto.RetirementAccountDTO;
import com.finova.account.dto.ContributionDTO;
import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.service.RetirementAccountService;
import com.finova.account.service.ContributionService;
import com.finova.account.service.IncomeSourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Account management operations
 * Handles retirement accounts, contributions, and income sources
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private RetirementAccountService retirementAccountService;
    
    @Autowired
    private ContributionService contributionService;
    
    @Autowired
    private IncomeSourceService incomeSourceService;

    /**
     * Health check endpoint
     */
    @GetMapping("/accounts/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Account Service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    // ========== RETIREMENT ACCOUNTS ENDPOINTS ==========
    
    /**
     * Get all retirement accounts for a user
     */
    @GetMapping("/accounts/user/{userId}")
    public ResponseEntity<List<RetirementAccountDTO>> getAccountsByUserId(@PathVariable Long userId) {
        List<RetirementAccountDTO> accounts = retirementAccountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * Get primary retirement account for a user (for dashboard)
     */
    @GetMapping("/accounts/user/{userId}/primary")
    public ResponseEntity<RetirementAccountDTO> getPrimaryAccount(@PathVariable Long userId) {
        RetirementAccountDTO account = retirementAccountService.getPrimaryAccountByUserId(userId);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Get retirement account by ID
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<RetirementAccountDTO> getAccountById(@PathVariable Long accountId) {
        RetirementAccountDTO account = retirementAccountService.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Create new retirement account
     */
    @PostMapping("/accounts")
    public ResponseEntity<RetirementAccountDTO> createAccount(@Valid @RequestBody RetirementAccountDTO accountDTO) {
        RetirementAccountDTO createdAccount = retirementAccountService.createAccount(accountDTO);
        return ResponseEntity.ok(createdAccount);
    }
    
    /**
     * Update retirement account
     */
    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<RetirementAccountDTO> updateAccount(
            @PathVariable Long accountId, 
            @Valid @RequestBody RetirementAccountDTO accountDTO) {
        RetirementAccountDTO updatedAccount = retirementAccountService.updateAccount(accountId, accountDTO);
        return ResponseEntity.ok(updatedAccount);
    }
    
    /**
     * Delete retirement account
     */
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        retirementAccountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
    
    // ========== CONTRIBUTIONS ENDPOINTS ==========
    
    /**
     * Get all contributions for a user
     */
    @GetMapping("/contributions/user/{userId}")
    public ResponseEntity<List<ContributionDTO>> getContributionsByUserId(@PathVariable Long userId) {
        List<ContributionDTO> contributions = contributionService.getContributionsByUserId(userId);
        return ResponseEntity.ok(contributions);
    }
    
    /**
     * Get all contributions for an account
     */
    @GetMapping("/contributions/account/{accountId}")
    public ResponseEntity<List<ContributionDTO>> getContributionsByAccountId(@PathVariable Long accountId) {
        List<ContributionDTO> contributions = contributionService.getContributionsByAccountId(accountId);
        return ResponseEntity.ok(contributions);
    }
    
    /**
     * Get contribution summary for a user
     */
    @GetMapping("/contributions/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getContributionsSummary(@PathVariable Long userId) {
        List<ContributionDTO> contributions = contributionService.getContributionsByUserId(userId);
        
        BigDecimal totalMonthly = contributions.stream()
            .map(ContributionDTO::getMonthlyAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalAnnual = totalMonthly.multiply(new BigDecimal("12"));
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("contributions", contributions);
        summary.put("totalMonthly", totalMonthly);
        summary.put("totalAnnual", totalAnnual);
        summary.put("totalMonthlyFormatted", String.format("$%,.0f", totalMonthly));
        summary.put("totalAnnualFormatted", String.format("$%,.0f", totalAnnual));
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Create new contribution
     */
    @PostMapping("/contributions")
    public ResponseEntity<ContributionDTO> createContribution(@Valid @RequestBody ContributionDTO contributionDTO) {
        ContributionDTO createdContribution = contributionService.createContribution(contributionDTO);
        return ResponseEntity.ok(createdContribution);
    }
    
    /**
     * Update contribution
     */
    @PutMapping("/contributions/{contributionId}")
    public ResponseEntity<ContributionDTO> updateContribution(
            @PathVariable Long contributionId, 
            @Valid @RequestBody ContributionDTO contributionDTO) {
        ContributionDTO updatedContribution = contributionService.updateContribution(contributionId, contributionDTO);
        return ResponseEntity.ok(updatedContribution);
    }
    
    /**
     * Update multiple contributions (for contributions management page)
     */
    @PutMapping("/contributions/account/{accountId}/bulk")
    public ResponseEntity<List<ContributionDTO>> updateContributions(
            @PathVariable Long accountId,
            @Valid @RequestBody List<ContributionDTO> contributions) {
        List<ContributionDTO> updatedContributions = contributionService.updateContributions(accountId, contributions);
        return ResponseEntity.ok(updatedContributions);
    }
    
    /**
     * Delete contribution
     */
    @DeleteMapping("/contributions/{contributionId}")
    public ResponseEntity<Void> deleteContribution(@PathVariable Long contributionId) {
        contributionService.deleteContribution(contributionId);
        return ResponseEntity.noContent().build();
    }
    
    // ========== INCOME SOURCES ENDPOINTS ==========
    
    /**
     * Get all income sources for a user
     */
    @GetMapping("/income-sources/user/{userId}")
    public ResponseEntity<List<IncomeSourceDTO>> getIncomeSourcesByUserId(@PathVariable Long userId) {
        List<IncomeSourceDTO> incomeSources = incomeSourceService.getIncomeSourcesByUserId(userId);
        return ResponseEntity.ok(incomeSources);
    }
    
    /**
     * Get income sources summary for a user
     */
    @GetMapping("/income-sources/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getIncomeSourcesSummary(@PathVariable Long userId) {
        List<IncomeSourceDTO> incomeSources = incomeSourceService.getIncomeSourcesByUserId(userId);
        
        BigDecimal totalProjectedIncome = incomeSources.stream()
            .map(IncomeSourceDTO::getProjectedMonthlyIncome)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCurrentBalance = incomeSources.stream()
            .map(IncomeSourceDTO::getCurrentBalance)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("incomeSources", incomeSources);
        summary.put("totalProjectedIncome", totalProjectedIncome);
        summary.put("totalCurrentBalance", totalCurrentBalance);
        summary.put("totalProjectedIncomeFormatted", String.format("$%,.0f", totalProjectedIncome));
        summary.put("totalCurrentBalanceFormatted", String.format("$%,.2f", totalCurrentBalance));
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Create new income source
     */
    @PostMapping("/income-sources")
    public ResponseEntity<IncomeSourceDTO> createIncomeSource(@Valid @RequestBody IncomeSourceDTO incomeSourceDTO) {
        IncomeSourceDTO createdIncomeSource = incomeSourceService.createIncomeSource(incomeSourceDTO);
        return ResponseEntity.ok(createdIncomeSource);
    }
    
    /**
     * Update income source
     */
    @PutMapping("/income-sources/{incomeSourceId}")
    public ResponseEntity<IncomeSourceDTO> updateIncomeSource(
            @PathVariable Long incomeSourceId, 
            @Valid @RequestBody IncomeSourceDTO incomeSourceDTO) {
        IncomeSourceDTO updatedIncomeSource = incomeSourceService.updateIncomeSource(incomeSourceId, incomeSourceDTO);
        return ResponseEntity.ok(updatedIncomeSource);
    }
    
    /**
     * Delete income source
     */
    @DeleteMapping("/income-sources/{incomeSourceId}")
    public ResponseEntity<Void> deleteIncomeSource(@PathVariable Long incomeSourceId) {
        incomeSourceService.deleteIncomeSource(incomeSourceId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get dashboard summary for a user (combines account, contributions, and income sources)
     */
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long userId) {
        RetirementAccountDTO primaryAccount = retirementAccountService.getPrimaryAccountByUserId(userId);
        List<ContributionDTO> contributions = contributionService.getContributionsByUserId(userId);
        List<IncomeSourceDTO> incomeSources = incomeSourceService.getIncomeSourcesByUserId(userId);
        
        // Calculate totals
        BigDecimal totalMonthlyContributions = contributions.stream()
            .map(ContributionDTO::getMonthlyAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalProjectedIncome = incomeSources.stream()
            .map(IncomeSourceDTO::getProjectedMonthlyIncome)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("primaryAccount", primaryAccount);
        dashboardData.put("contributions", contributions);
        dashboardData.put("incomeSources", incomeSources);
        dashboardData.put("totalMonthlyContributions", totalMonthlyContributions);
        dashboardData.put("totalProjectedIncome", totalProjectedIncome);
        dashboardData.put("status", primaryAccount != null ? 
            (primaryAccount.isOnTrack() ? "You are on track! ✓" : "Behind schedule") : "No data");
        
        return ResponseEntity.ok(dashboardData);
    }
}
