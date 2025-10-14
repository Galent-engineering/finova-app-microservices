package com.finova.account.service;

import com.finova.account.dto.RetirementAccountDTO;
import com.finova.account.dto.ContributionDTO;
import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.RetirementAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for RetirementAccount operations
 */
@Service
@Transactional
public class RetirementAccountService {
    
    @Autowired
    private RetirementAccountRepository retirementAccountRepository;
    
    @Autowired
    private ContributionService contributionService;
    
    @Autowired
    private IncomeSourceService incomeSourceService;
    
    /**
     * Get all retirement accounts for a user
     */
    public List<RetirementAccountDTO> getAccountsByUserId(Long userId) {
        List<RetirementAccount> accounts = retirementAccountRepository.findByUserId(userId);
        return accounts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get primary retirement account for a user (for dashboard)
     * If none exists, creates a default account with sample data
     */
    public RetirementAccountDTO getPrimaryAccountByUserId(Long userId) {
        RetirementAccount account = retirementAccountRepository.findPrimaryByUserId(userId)
            .orElseGet(() -> createDefaultAccount(userId));
        return convertToDTOWithDetails(account);
    }
    
    /**
     * Get retirement account by ID
     */
    public RetirementAccountDTO getAccountById(Long accountId) {
        RetirementAccount account = retirementAccountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        return convertToDTOWithDetails(account);
    }
    
    /**
     * Create new retirement account
     */
    public RetirementAccountDTO createAccount(RetirementAccountDTO accountDTO) {
        RetirementAccount account = convertToEntity(accountDTO);
        RetirementAccount savedAccount = retirementAccountRepository.save(account);
        return convertToDTO(savedAccount);
    }
    
    /**
     * Update retirement account
     */
    public RetirementAccountDTO updateAccount(Long accountId, RetirementAccountDTO accountDTO) {
        RetirementAccount existingAccount = retirementAccountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        
        // Update fields
        existingAccount.setAccountName(accountDTO.getAccountName());
        existingAccount.setAccountType(accountDTO.getAccountType());
        existingAccount.setCurrentBalance(accountDTO.getCurrentBalance());
        existingAccount.setAsOfDate(accountDTO.getAsOfDate());
        existingAccount.setEstimatedMonthlyIncome(accountDTO.getEstimatedMonthlyIncome());
        existingAccount.setEstimatedMonthlyGoal(accountDTO.getEstimatedMonthlyGoal());
        existingAccount.setStatus(accountDTO.getStatus());
        
        RetirementAccount savedAccount = retirementAccountRepository.save(existingAccount);
        return convertToDTO(savedAccount);
    }
    
    /**
     * Delete retirement account
     */
    public void deleteAccount(Long accountId) {
        if (!retirementAccountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
        retirementAccountRepository.deleteById(accountId);
    }
    
    /**
     * Create a default retirement account with sample data for demo purposes
     */
    private RetirementAccount createDefaultAccount(Long userId) {
        RetirementAccount account = new RetirementAccount();
        account.setUserId(userId);
        account.setAccountName("New York City Deferred Compensation 401(a) Plan");
        account.setAccountType("401a");
        account.setCurrentBalance(new BigDecimal("106965.67"));
        account.setEstimatedMonthlyIncome(new BigDecimal("6965"));
        account.setEstimatedMonthlyGoal(new BigDecimal("6210"));
        account.setStatus("on_track");
        account.setAsOfDate(LocalDate.now());
        
        return retirementAccountRepository.save(account);
    }
    
    /**
     * Convert RetirementAccount entity to DTO
     */
    private RetirementAccountDTO convertToDTO(RetirementAccount account) {
        RetirementAccountDTO dto = new RetirementAccountDTO();
        dto.setId(account.getId());
        dto.setUserId(account.getUserId());
        dto.setAccountName(account.getAccountName());
        dto.setAccountType(account.getAccountType());
        dto.setCurrentBalance(account.getCurrentBalance());
        dto.setAsOfDate(account.getAsOfDate());
        dto.setEstimatedMonthlyIncome(account.getEstimatedMonthlyIncome());
        dto.setEstimatedMonthlyGoal(account.getEstimatedMonthlyGoal());
        dto.setStatus(account.getStatus());
        return dto;
    }
    
    /**
     * Convert RetirementAccount entity to DTO with contributions and income sources
     */
    private RetirementAccountDTO convertToDTOWithDetails(RetirementAccount account) {
        RetirementAccountDTO dto = convertToDTO(account);
        
        // Load related data
        List<ContributionDTO> contributions = contributionService.getContributionsByAccountId(account.getId());
        List<IncomeSourceDTO> incomeSources = incomeSourceService.getIncomeSourcesByAccountId(account.getId());
        
        dto.setContributions(contributions);
        dto.setIncomeSources(incomeSources);
        
        return dto;
    }
    
    /**
     * Convert RetirementAccountDTO to entity
     */
    private RetirementAccount convertToEntity(RetirementAccountDTO dto) {
        RetirementAccount account = new RetirementAccount();
        account.setId(dto.getId());
        account.setUserId(dto.getUserId());
        account.setAccountName(dto.getAccountName());
        account.setAccountType(dto.getAccountType());
        account.setCurrentBalance(dto.getCurrentBalance());
        account.setAsOfDate(dto.getAsOfDate());
        account.setEstimatedMonthlyIncome(dto.getEstimatedMonthlyIncome());
        account.setEstimatedMonthlyGoal(dto.getEstimatedMonthlyGoal());
        account.setStatus(dto.getStatus());
        return account;
    }
}
