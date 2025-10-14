package com.finova.account.service;

import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.model.IncomeSource;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.IncomeSourceRepository;
import com.finova.account.repository.RetirementAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for IncomeSource operations
 */
@Service
@Transactional
public class IncomeSourceService {
    
    @Autowired
    private IncomeSourceRepository incomeSourceRepository;
    
    @Autowired
    private RetirementAccountRepository retirementAccountRepository;
    
    /**
     * Get all income sources for a user
     */
    public List<IncomeSourceDTO> getIncomeSourcesByUserId(Long userId) {
        List<IncomeSource> incomeSources = incomeSourceRepository.findActiveByUserId(userId);
        
        // If no income sources exist, create sample ones
        if (incomeSources.isEmpty()) {
            incomeSources = createSampleIncomeSources(userId);
        }
        
        return incomeSources.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all income sources for an account
     */
    public List<IncomeSourceDTO> getIncomeSourcesByAccountId(Long accountId) {
        List<IncomeSource> incomeSources = incomeSourceRepository.findByAccountId(accountId);
        return incomeSources.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Create new income source
     */
    public IncomeSourceDTO createIncomeSource(IncomeSourceDTO incomeSourceDTO) {
        IncomeSource incomeSource = convertToEntity(incomeSourceDTO);
        IncomeSource savedIncomeSource = incomeSourceRepository.save(incomeSource);
        return convertToDTO(savedIncomeSource);
    }
    
    /**
     * Update income source
     */
    public IncomeSourceDTO updateIncomeSource(Long incomeSourceId, IncomeSourceDTO incomeSourceDTO) {
        IncomeSource existingIncomeSource = incomeSourceRepository.findById(incomeSourceId)
            .orElseThrow(() -> new RuntimeException("Income source not found with id: " + incomeSourceId));
        
        // Update fields
        existingIncomeSource.setSourceType(incomeSourceDTO.getSourceType());
        existingIncomeSource.setSourceName(incomeSourceDTO.getSourceName());
        existingIncomeSource.setCurrentBalance(incomeSourceDTO.getCurrentBalance());
        existingIncomeSource.setProjectedMonthlyIncome(incomeSourceDTO.getProjectedMonthlyIncome());
        existingIncomeSource.setProvider(incomeSourceDTO.getProvider());
        existingIncomeSource.setActive(incomeSourceDTO.isActive());
        
        IncomeSource savedIncomeSource = incomeSourceRepository.save(existingIncomeSource);
        return convertToDTO(savedIncomeSource);
    }
    
    /**
     * Delete income source
     */
    public void deleteIncomeSource(Long incomeSourceId) {
        if (!incomeSourceRepository.existsById(incomeSourceId)) {
            throw new RuntimeException("Income source not found with id: " + incomeSourceId);
        }
        incomeSourceRepository.deleteById(incomeSourceId);
    }
    
    /**
     * Create sample income sources for demo purposes
     */
    private List<IncomeSource> createSampleIncomeSources(Long userId) {
        // Find the primary account for this user
        RetirementAccount primaryAccount = retirementAccountRepository.findPrimaryByUserId(userId)
            .orElse(null);
        
        if (primaryAccount == null) {
            return new ArrayList<>();
        }
        
        List<IncomeSource> incomeSources = new ArrayList<>();
        
        // 401k - Finova
        IncomeSource source1 = new IncomeSource();
        source1.setSourceType("401k");
        source1.setSourceName("401(k) - Finova");
        source1.setCurrentBalance(new BigDecimal("106965.67"));
        source1.setProjectedMonthlyIncome(new BigDecimal("2980"));
        source1.setProvider("Finova");
        source1.setAccount(primaryAccount);
        incomeSources.add(incomeSourceRepository.save(source1));
        
        // Traditional IRA - Fidelity
        IncomeSource source2 = new IncomeSource();
        source2.setSourceType("ira");
        source2.setSourceName("Traditional IRA - Fidelity");
        source2.setCurrentBalance(new BigDecimal("45230.00"));
        source2.setProjectedMonthlyIncome(new BigDecimal("1285"));
        source2.setProvider("Fidelity");
        source2.setAccount(primaryAccount);
        incomeSources.add(incomeSourceRepository.save(source2));
        
        // Pension
        IncomeSource source3 = new IncomeSource();
        source3.setSourceType("pension");
        source3.setSourceName("Pension (estimated monthly)");
        source3.setCurrentBalance(new BigDecimal("0"));
        source3.setProjectedMonthlyIncome(new BigDecimal("1850"));
        source3.setAccount(primaryAccount);
        incomeSources.add(incomeSourceRepository.save(source3));
        
        return incomeSources;
    }
    
    /**
     * Convert IncomeSource entity to DTO
     */
    private IncomeSourceDTO convertToDTO(IncomeSource incomeSource) {
        IncomeSourceDTO dto = new IncomeSourceDTO();
        dto.setId(incomeSource.getId());
        dto.setAccountId(incomeSource.getAccount() != null ? incomeSource.getAccount().getId() : null);
        dto.setSourceType(incomeSource.getSourceType());
        dto.setSourceName(incomeSource.getSourceName());
        dto.setCurrentBalance(incomeSource.getCurrentBalance());
        dto.setProjectedMonthlyIncome(incomeSource.getProjectedMonthlyIncome());
        dto.setProvider(incomeSource.getProvider());
        dto.setActive(incomeSource.isActive());
        return dto;
    }
    
    /**
     * Convert IncomeSourceDTO to entity
     */
    private IncomeSource convertToEntity(IncomeSourceDTO dto) {
        IncomeSource incomeSource = new IncomeSource();
        incomeSource.setId(dto.getId());
        incomeSource.setSourceType(dto.getSourceType());
        incomeSource.setSourceName(dto.getSourceName());
        incomeSource.setCurrentBalance(dto.getCurrentBalance());
        incomeSource.setProjectedMonthlyIncome(dto.getProjectedMonthlyIncome());
        incomeSource.setProvider(dto.getProvider());
        incomeSource.setActive(dto.isActive());
        
        // Set the account if accountId is provided
        if (dto.getAccountId() != null) {
            RetirementAccount account = retirementAccountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + dto.getAccountId()));
            incomeSource.setAccount(account);
        }
        
        return incomeSource;
    }
}
