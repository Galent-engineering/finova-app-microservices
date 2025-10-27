package com.finova.account.service;

import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.model.IncomeSource;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.IncomeSourceRepository;
import com.finova.account.repository.RetirementAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IncomeSourceService
 */
@ExtendWith(MockitoExtension.class)
class IncomeSourceServiceTest {

    @Mock
    private IncomeSourceRepository incomeSourceRepository;

    @Mock
    private RetirementAccountRepository retirementAccountRepository;

    @InjectMocks
    private IncomeSourceService incomeSourceService;

    private IncomeSource testIncomeSource;
    private IncomeSourceDTO testIncomeSourceDTO;
    private RetirementAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = createTestAccount();
        testIncomeSource = createTestIncomeSource();
        testIncomeSourceDTO = createTestIncomeSourceDTO();
    }

    @Test
    void getIncomeSourcesByUserId_WhenIncomeSourcesExist_ShouldReturnIncomeSourceDTOs() {
        // Given
        List<IncomeSource> incomeSources = List.of(testIncomeSource);
        when(incomeSourceRepository.findActiveByUserId(1L)).thenReturn(incomeSources);

        // When
        List<IncomeSourceDTO> result = incomeSourceService.getIncomeSourcesByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testIncomeSource.getId(), result.get(0).getId());
        assertEquals(testIncomeSource.getSourceType(), result.get(0).getSourceType());
        verify(incomeSourceRepository).findActiveByUserId(1L);
    }

    @Test
    void getIncomeSourcesByUserId_WhenNoIncomeSourcesExist_ShouldCreateSampleIncomeSources() {
        // Given
        when(incomeSourceRepository.findActiveByUserId(1L)).thenReturn(new ArrayList<>());
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.of(testAccount));
        when(incomeSourceRepository.save(any(IncomeSource.class))).thenReturn(testIncomeSource);

        // When
        List<IncomeSourceDTO> result = incomeSourceService.getIncomeSourcesByUserId(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(incomeSourceRepository).findActiveByUserId(1L);
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
        verify(incomeSourceRepository, atLeastOnce()).save(any(IncomeSource.class));
    }

    @Test
    void getIncomeSourcesByAccountId_ShouldReturnIncomeSourceDTOs() {
        // Given
        List<IncomeSource> incomeSources = List.of(testIncomeSource);
        when(incomeSourceRepository.findByAccountId(1L)).thenReturn(incomeSources);

        // When
        List<IncomeSourceDTO> result = incomeSourceService.getIncomeSourcesByAccountId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testIncomeSource.getId(), result.get(0).getId());
        verify(incomeSourceRepository).findByAccountId(1L);
    }

    @Test
    void createIncomeSource_WithValidData_ShouldReturnCreatedIncomeSourceDTO() {
        // Given
        when(retirementAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(incomeSourceRepository.save(any(IncomeSource.class))).thenReturn(testIncomeSource);

        // When
        IncomeSourceDTO result = incomeSourceService.createIncomeSource(testIncomeSourceDTO);

        // Then
        assertNotNull(result);
        assertEquals(testIncomeSource.getId(), result.getId());
        assertEquals(testIncomeSource.getSourceType(), result.getSourceType());
        verify(retirementAccountRepository).findById(1L);
        verify(incomeSourceRepository).save(any(IncomeSource.class));
    }

    @Test
    void createIncomeSource_WithInvalidAccountId_ShouldThrowRuntimeException() {
        // Given
        when(retirementAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> incomeSourceService.createIncomeSource(testIncomeSourceDTO));
        
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(retirementAccountRepository).findById(999L);
        verify(incomeSourceRepository, never()).save(any(IncomeSource.class));
    }

    @Test
    void updateIncomeSource_WhenIncomeSourceExists_ShouldReturnUpdatedIncomeSourceDTO() {
        // Given
        when(incomeSourceRepository.findById(1L)).thenReturn(Optional.of(testIncomeSource));
        when(incomeSourceRepository.save(any(IncomeSource.class))).thenReturn(testIncomeSource);

        // When
        IncomeSourceDTO result = incomeSourceService.updateIncomeSource(1L, testIncomeSourceDTO);

        // Then
        assertNotNull(result);
        assertEquals(testIncomeSource.getId(), result.getId());
        assertEquals(testIncomeSourceDTO.getSourceType(), testIncomeSource.getSourceType());
        verify(incomeSourceRepository).findById(1L);
        verify(incomeSourceRepository).save(testIncomeSource);
    }

    @Test
    void updateIncomeSource_WhenIncomeSourceNotFound_ShouldThrowRuntimeException() {
        // Given
        when(incomeSourceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> incomeSourceService.updateIncomeSource(999L, testIncomeSourceDTO));
        
        assertEquals("Income source not found with id: 999", exception.getMessage());
        verify(incomeSourceRepository).findById(999L);
        verify(incomeSourceRepository, never()).save(any(IncomeSource.class));
    }

    @Test
    void deleteIncomeSource_WhenIncomeSourceExists_ShouldDeleteIncomeSource() {
        // Given
        when(incomeSourceRepository.existsById(1L)).thenReturn(true);

        // When
        incomeSourceService.deleteIncomeSource(1L);

        // Then
        verify(incomeSourceRepository).existsById(1L);
        verify(incomeSourceRepository).deleteById(1L);
    }

    @Test
    void deleteIncomeSource_WhenIncomeSourceNotFound_ShouldThrowRuntimeException() {
        // Given
        when(incomeSourceRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> incomeSourceService.deleteIncomeSource(999L));
        
        assertEquals("Income source not found with id: 999", exception.getMessage());
        verify(incomeSourceRepository).existsById(999L);
        verify(incomeSourceRepository, never()).deleteById(anyLong());
    }

    @Test
    void createSampleIncomeSources_WhenNoPrimaryAccount_ShouldReturnEmptyList() {
        // Given
        when(incomeSourceRepository.findActiveByUserId(1L)).thenReturn(new ArrayList<>());
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.empty());

        // When
        List<IncomeSourceDTO> result = incomeSourceService.getIncomeSourcesByUserId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(incomeSourceRepository).findActiveByUserId(1L);
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
        verify(incomeSourceRepository, never()).save(any(IncomeSource.class));
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

    private IncomeSource createTestIncomeSource() {
        IncomeSource incomeSource = new IncomeSource();
        incomeSource.setId(1L);
        incomeSource.setAccount(testAccount);
        incomeSource.setSourceType("401k");
        incomeSource.setSourceName("Test 401(k)");
        incomeSource.setCurrentBalance(new BigDecimal("100000.00"));
        incomeSource.setProjectedMonthlyIncome(new BigDecimal("3000.00"));
        incomeSource.setProvider("Test Provider");
        incomeSource.setActive(true);
        return incomeSource;
    }

    private IncomeSourceDTO createTestIncomeSourceDTO() {
        IncomeSourceDTO dto = new IncomeSourceDTO();
        dto.setId(1L);
        dto.setAccountId(1L);
        dto.setSourceType("401k");
        dto.setSourceName("Test 401(k)");
        dto.setCurrentBalance(new BigDecimal("100000.00"));
        dto.setProjectedMonthlyIncome(new BigDecimal("3000.00"));
        dto.setProvider("Test Provider");
        dto.setActive(true);
        return dto;
    }
}
