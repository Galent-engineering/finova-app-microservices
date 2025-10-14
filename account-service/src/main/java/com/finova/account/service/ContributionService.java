package com.finova.account.service;

import com.finova.account.dto.ContributionDTO;
import com.finova.account.model.Contribution;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.ContributionRepository;
import com.finova.account.repository.RetirementAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Contribution operations
 */
@Service
@Transactional
public class ContributionService {
    
    @Autowired
    private ContributionRepository contributionRepository;
    
    @Autowired
    private RetirementAccountRepository retirementAccountRepository;
    
    /**
     * Get all contributions for a user
     */
    public List<ContributionDTO> getContributionsByUserId(Long userId) {
        List<Contribution> contributions = contributionRepository.findByUserId(userId);
        
        // If no contributions exist, create sample ones
        if (contributions.isEmpty()) {
            contributions = createSampleContributions(userId);
        }
        
        return contributions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all contributions for an account
     */
    public List<ContributionDTO> getContributionsByAccountId(Long accountId) {
        List<Contribution> contributions = contributionRepository.findByAccountId(accountId);
        return contributions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Create new contribution
     */
    public ContributionDTO createContribution(ContributionDTO contributionDTO) {
        Contribution contribution = convertToEntity(contributionDTO);
        Contribution savedContribution = contributionRepository.save(contribution);
        return convertToDTO(savedContribution);
    }
    
    /**
     * Update contribution
     */
    public ContributionDTO updateContribution(Long contributionId, ContributionDTO contributionDTO) {
        Contribution existingContribution = contributionRepository.findById(contributionId)
            .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + contributionId));
        
        // Update fields
        existingContribution.setContributionType(contributionDTO.getContributionType());
        existingContribution.setPercentage(contributionDTO.getPercentage());
        existingContribution.setMonthlyAmount(contributionDTO.getMonthlyAmount());
        existingContribution.setAnnualAmount(contributionDTO.getAnnualAmount());
        existingContribution.setEffectiveDate(contributionDTO.getEffectiveDate());
        
        Contribution savedContribution = contributionRepository.save(existingContribution);
        return convertToDTO(savedContribution);
    }
    
    /**
     * Update multiple contributions (for bulk operations)
     */
    public List<ContributionDTO> updateContributions(Long accountId, List<ContributionDTO> contributionDTOs) {
        List<Contribution> updatedContributions = new ArrayList<>();
        
        for (ContributionDTO dto : contributionDTOs) {
            if (dto.getId() != null) {
                // Update existing contribution
                ContributionDTO updated = updateContribution(dto.getId(), dto);
                updatedContributions.add(convertToEntity(updated));
            } else {
                // Create new contribution
                dto.setAccountId(accountId);
                Contribution created = convertToEntity(dto);
                updatedContributions.add(contributionRepository.save(created));
            }
        }
        
        return updatedContributions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Delete contribution
     */
    public void deleteContribution(Long contributionId) {
        if (!contributionRepository.existsById(contributionId)) {
            throw new RuntimeException("Contribution not found with id: " + contributionId);
        }
        contributionRepository.deleteById(contributionId);
    }
    
    /**
     * Create sample contributions for demo purposes
     */
    private List<Contribution> createSampleContributions(Long userId) {
        // Find the primary account for this user
        RetirementAccount primaryAccount = retirementAccountRepository.findPrimaryByUserId(userId)
            .orElse(null);
        
        if (primaryAccount == null) {
            return new ArrayList<>();
        }
        
        List<Contribution> contributions = new ArrayList<>();
        
        // Pre-Tax Contribution
        Contribution preTax = new Contribution();
        preTax.setContributionType("pre_tax");
        preTax.setPercentage(new BigDecimal("8"));
        preTax.setMonthlyAmount(new BigDecimal("520"));
        preTax.setAnnualAmount(new BigDecimal("6240"));
        preTax.setAccount(primaryAccount);
        preTax.setEffectiveDate(LocalDate.now());
        contributions.add(contributionRepository.save(preTax));
        
        // Roth Contribution
        Contribution roth = new Contribution();
        roth.setContributionType("roth");
        roth.setPercentage(new BigDecimal("2"));
        roth.setMonthlyAmount(new BigDecimal("130"));
        roth.setAnnualAmount(new BigDecimal("1560"));
        roth.setAccount(primaryAccount);
        roth.setEffectiveDate(LocalDate.now());
        contributions.add(contributionRepository.save(roth));
        
        // Employer Match
        Contribution employerMatch = new Contribution();
        employerMatch.setContributionType("employer_match");
        employerMatch.setPercentage(new BigDecimal("5"));
        employerMatch.setMonthlyAmount(new BigDecimal("325"));
        employerMatch.setAnnualAmount(new BigDecimal("3900"));
        employerMatch.setAccount(primaryAccount);
        employerMatch.setEffectiveDate(LocalDate.now());
        contributions.add(contributionRepository.save(employerMatch));
        
        return contributions;
    }
    
    /**
     * Convert Contribution entity to DTO
     */
    private ContributionDTO convertToDTO(Contribution contribution) {
        ContributionDTO dto = new ContributionDTO();
        dto.setId(contribution.getId());
        dto.setAccountId(contribution.getAccount().getId());
        dto.setContributionType(contribution.getContributionType());
        dto.setPercentage(contribution.getPercentage());
        dto.setMonthlyAmount(contribution.getMonthlyAmount());
        dto.setAnnualAmount(contribution.getAnnualAmount());
        dto.setEffectiveDate(contribution.getEffectiveDate());
        return dto;
    }
    
    /**
     * Convert ContributionDTO to entity
     */
    private Contribution convertToEntity(ContributionDTO dto) {
        Contribution contribution = new Contribution();
        contribution.setId(dto.getId());
        contribution.setContributionType(dto.getContributionType());
        contribution.setPercentage(dto.getPercentage());
        contribution.setMonthlyAmount(dto.getMonthlyAmount());
        contribution.setAnnualAmount(dto.getAnnualAmount());
        contribution.setEffectiveDate(dto.getEffectiveDate());
        
        // Set the account if accountId is provided
        if (dto.getAccountId() != null) {
            RetirementAccount account = retirementAccountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + dto.getAccountId()));
            contribution.setAccount(account);
        }
        
        return contribution;
    }
}
