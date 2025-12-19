package com.finova.account.service;

import com.finova.account.dto.ContributionDTO;
import com.finova.account.model.Contribution;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.ContributionRepository;
import com.finova.account.repository.RetirementAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContributionService
 */
@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private RetirementAccountRepository retirementAccountRepository;

    @InjectMocks
    private ContributionService contributionService;

    private Contribution testContribution;
    private ContributionDTO testContributionDTO;
    private RetirementAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = createTestAccount();
        testContribution = createTestContribution();
        testContributionDTO = createTestContributionDTO();
    }

    @Test
    void getContributionsByUserId_WhenContributionsExist_ShouldReturnContributionDTOs() {
        // Given
        List<Contribution> contributions = List.of(testContribution);
        when(contributionRepository.findByUserId(1L)).thenReturn(contributions);

        // When
        List<ContributionDTO> result = contributionService.getContributionsByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContribution.getId(), result.get(0).getId());
        assertEquals(testContribution.getContributionType(), result.get(0).getContributionType());
        verify(contributionRepository).findByUserId(1L);
    }

    @Test
    void getContributionsByUserId_WhenNoContributionsExist_ShouldCreateSampleContributions() {
        // Given
        when(contributionRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.of(testAccount));
        when(contributionRepository.save(any(Contribution.class))).thenReturn(testContribution);

        // When
        List<ContributionDTO> result = contributionService.getContributionsByUserId(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(contributionRepository).findByUserId(1L);
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
        verify(contributionRepository, atLeastOnce()).save(any(Contribution.class));
    }

    @Test
    void getContributionsByAccountId_ShouldReturnContributionDTOs() {
        // Given
        List<Contribution> contributions = List.of(testContribution);
        when(contributionRepository.findByAccountId(1L)).thenReturn(contributions);

        // When
        List<ContributionDTO> result = contributionService.getContributionsByAccountId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContribution.getId(), result.get(0).getId());
        verify(contributionRepository).findByAccountId(1L);
    }

    @Test
    void createContribution_WithValidData_ShouldReturnCreatedContributionDTO() {
        // Given
        when(retirementAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(contributionRepository.save(any(Contribution.class))).thenReturn(testContribution);

        // When
        ContributionDTO result = contributionService.createContribution(testContributionDTO);

        // Then
        assertNotNull(result);
        assertEquals(testContribution.getId(), result.getId());
        assertEquals(testContribution.getContributionType(), result.getContributionType());
        verify(retirementAccountRepository).findById(1L);
        verify(contributionRepository).save(any(Contribution.class));
    }

    @Test
    void createContribution_WithInvalidAccountId_ShouldThrowRuntimeException() {
        // Given
        ContributionDTO invalidDTO = createTestContributionDTO();
        invalidDTO.setAccountId(999L);
        when(retirementAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contributionService.createContribution(invalidDTO));
        
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(retirementAccountRepository).findById(999L);
        verify(contributionRepository, never()).save(any(Contribution.class));
    }

    @Test
    void updateContribution_WhenContributionExists_ShouldReturnUpdatedContributionDTO() {
        // Given
        when(contributionRepository.findById(1L)).thenReturn(Optional.of(testContribution));
        when(contributionRepository.save(any(Contribution.class))).thenReturn(testContribution);

        // When
        ContributionDTO result = contributionService.updateContribution(1L, testContributionDTO);

        // Then
        assertNotNull(result);
        assertEquals(testContribution.getId(), result.getId());
        assertEquals(testContributionDTO.getContributionType(), testContribution.getContributionType());
        verify(contributionRepository).findById(1L);
        verify(contributionRepository).save(testContribution);
    }

    @Test
    void updateContribution_WhenContributionNotFound_ShouldThrowRuntimeException() {
        // Given
        when(contributionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contributionService.updateContribution(999L, testContributionDTO));
        
        assertEquals("Contribution not found with id: 999", exception.getMessage());
        verify(contributionRepository).findById(999L);
        verify(contributionRepository, never()).save(any(Contribution.class));
    }

    @Test
    void updateContributions_WithMixedData_ShouldHandleBothUpdatesAndCreations() {
        // Given
        List<ContributionDTO> contributionDTOs = List.of(testContributionDTO);
        when(contributionRepository.findById(1L)).thenReturn(Optional.of(testContribution));
        when(retirementAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(contributionRepository.save(any(Contribution.class))).thenReturn(testContribution);

        // When
        List<ContributionDTO> result = contributionService.updateContributions(1L, contributionDTOs);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(contributionRepository).findById(1L);
        verify(retirementAccountRepository).findById(1L);
        verify(contributionRepository).save(any(Contribution.class));
    }

    @Test
    void deleteContribution_WhenContributionExists_ShouldDeleteContribution() {
        // Given
        when(contributionRepository.existsById(1L)).thenReturn(true);

        // When
        contributionService.deleteContribution(1L);

        // Then
        verify(contributionRepository).existsById(1L);
        verify(contributionRepository).deleteById(1L);
    }

    @Test
    void deleteContribution_WhenContributionNotFound_ShouldThrowRuntimeException() {
        // Given
        when(contributionRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contributionService.deleteContribution(999L));
        
        assertEquals("Contribution not found with id: 999", exception.getMessage());
        verify(contributionRepository).existsById(999L);
        verify(contributionRepository, never()).deleteById(anyLong());
    }

    @Test
    void createSampleContributions_WhenNoPrimaryAccount_ShouldReturnEmptyList() {
        // Given
        when(contributionRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.empty());

        // When
        List<ContributionDTO> result = contributionService.getContributionsByUserId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contributionRepository).findByUserId(1L);
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
        verify(contributionRepository, never()).save(any(Contribution.class));
    }

    private RetirementAccount createTestAccount() {
        RetirementAccount account = new RetirementAccount();
        account.setId(1L);
        account.setUserId(1L);
        account.setAccountName("Test 401(k) Account");
        account.setAccountType("401k");
        account.setCurrentBalance(new BigDecimal("100000.00"));
        return account;
    }

    private Contribution createTestContribution() {
        Contribution contribution = new Contribution();
        contribution.setId(1L);
        contribution.setAccount(testAccount);
        contribution.setContributionType("pre_tax");
        contribution.setPercentage(new BigDecimal("8.0"));
        contribution.setMonthlyAmount(new BigDecimal("520.00"));
        contribution.setAnnualAmount(new BigDecimal("6240.00"));
        contribution.setEffectiveDate(LocalDate.now());
        return contribution;
    }

    private ContributionDTO createTestContributionDTO() {
        ContributionDTO dto = new ContributionDTO();
        dto.setId(1L);
        dto.setAccountId(1L);
        dto.setContributionType("pre_tax");
        dto.setPercentage(new BigDecimal("8.0"));
        dto.setMonthlyAmount(new BigDecimal("520.00"));
        dto.setAnnualAmount(new BigDecimal("6240.00"));
        dto.setEffectiveDate(LocalDate.now());
        return dto;
    }
}
