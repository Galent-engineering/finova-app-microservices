package com.finova.account.service;

import com.finova.account.dto.RetirementAccountDTO;
import com.finova.account.model.RetirementAccount;
import com.finova.account.repository.RetirementAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RetirementAccountService
 */
@ExtendWith(MockitoExtension.class)
class RetirementAccountServiceTest {

    @Mock
    private RetirementAccountRepository retirementAccountRepository;

    @Mock
    private ContributionService contributionService;

    @Mock
    private IncomeSourceService incomeSourceService;

    @InjectMocks
    private RetirementAccountService retirementAccountService;

    private RetirementAccount testAccount;
    private RetirementAccountDTO testAccountDTO;

    @BeforeEach
    void setUp() {
        testAccount = createTestAccount();
        testAccountDTO = createTestAccountDTO();
    }

    @Test
    void getAccountsByUserId_WhenAccountsExist_ShouldReturnAccountDTOs() {
        // Given
        List<RetirementAccount> accounts = List.of(testAccount);
        when(retirementAccountRepository.findByUserId(1L)).thenReturn(accounts);

        // When
        List<RetirementAccountDTO> result = retirementAccountService.getAccountsByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount.getId(), result.get(0).getId());
        assertEquals(testAccount.getAccountName(), result.get(0).getAccountName());
        verify(retirementAccountRepository).findByUserId(1L);
    }

    @Test
    void getAccountsByUserId_WhenNoAccountsExist_ShouldReturnEmptyList() {
        // Given
        when(retirementAccountRepository.findByUserId(1L)).thenReturn(List.of());

        // When
        List<RetirementAccountDTO> result = retirementAccountService.getAccountsByUserId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(retirementAccountRepository).findByUserId(1L);
    }

    @Test
    void getPrimaryAccountByUserId_WhenAccountExists_ShouldReturnAccountDTO() {
        // Given
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.of(testAccount));

        // When
        RetirementAccountDTO result = retirementAccountService.getPrimaryAccountByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getAccountName(), result.getAccountName());
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
    }

    @Test
    void getPrimaryAccountByUserId_WhenNoAccountExists_ShouldCreateDefaultAccount() {
        // Given
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.empty());
        when(retirementAccountRepository.save(any(RetirementAccount.class))).thenReturn(testAccount);

        // When
        RetirementAccountDTO result = retirementAccountService.getPrimaryAccountByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        verify(retirementAccountRepository).findPrimaryByUserId(1L);
        verify(retirementAccountRepository).save(any(RetirementAccount.class));
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccountDTO() {
        // Given
        when(retirementAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        RetirementAccountDTO result = retirementAccountService.getAccountById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getAccountName(), result.getAccountName());
        verify(retirementAccountRepository).findById(1L);
    }

    @Test
    void getAccountById_WhenAccountNotFound_ShouldThrowRuntimeException() {
        // Given
        when(retirementAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> retirementAccountService.getAccountById(999L));
        
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(retirementAccountRepository).findById(999L);
    }

    @Test
    void createAccount_WithValidData_ShouldReturnCreatedAccountDTO() {
        // Given
        when(retirementAccountRepository.save(any(RetirementAccount.class))).thenReturn(testAccount);

        // When
        RetirementAccountDTO result = retirementAccountService.createAccount(testAccountDTO);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getAccountName(), result.getAccountName());
        verify(retirementAccountRepository).save(any(RetirementAccount.class));
    }

    @Test
    void updateAccount_WhenAccountExists_ShouldReturnUpdatedAccountDTO() {
        // Given
        when(retirementAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(retirementAccountRepository.save(any(RetirementAccount.class))).thenReturn(testAccount);

        // When
        RetirementAccountDTO result = retirementAccountService.updateAccount(1L, testAccountDTO);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccountDTO.getAccountName(), testAccount.getAccountName());
        assertEquals(testAccountDTO.getAccountType(), testAccount.getAccountType());
        verify(retirementAccountRepository).findById(1L);
        verify(retirementAccountRepository).save(testAccount);
    }

    @Test
    void updateAccount_WhenAccountNotFound_ShouldThrowRuntimeException() {
        // Given
        when(retirementAccountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> retirementAccountService.updateAccount(999L, testAccountDTO));
        
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(retirementAccountRepository).findById(999L);
        verify(retirementAccountRepository, never()).save(any(RetirementAccount.class));
    }

    @Test
    void deleteAccount_WhenAccountExists_ShouldDeleteAccount() {
        // Given
        when(retirementAccountRepository.existsById(1L)).thenReturn(true);

        // When
        retirementAccountService.deleteAccount(1L);

        // Then
        verify(retirementAccountRepository).existsById(1L);
        verify(retirementAccountRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_WhenAccountNotFound_ShouldThrowRuntimeException() {
        // Given
        when(retirementAccountRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> retirementAccountService.deleteAccount(999L));
        
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(retirementAccountRepository).existsById(999L);
        verify(retirementAccountRepository, never()).deleteById(anyLong());
    }

    @Test
    void createDefaultAccount_ShouldCreateAccountWithCorrectData() {
        // Given
        when(retirementAccountRepository.save(any(RetirementAccount.class))).thenReturn(testAccount);

        // When
        RetirementAccountDTO result = retirementAccountService.getPrimaryAccountByUserId(1L);
        when(retirementAccountRepository.findPrimaryByUserId(1L)).thenReturn(Optional.empty());

        // Then
        result = retirementAccountService.getPrimaryAccountByUserId(1L);
        assertNotNull(result);
        verify(retirementAccountRepository).save(any(RetirementAccount.class));
    }

    private RetirementAccount createTestAccount() {
        RetirementAccount account = new RetirementAccount();
        account.setId(1L);
        account.setUserId(1L);
        account.setAccountName("Test 401(k) Account");
        account.setAccountType("401k");
        account.setCurrentBalance(new BigDecimal("100000.00"));
        account.setAsOfDate(LocalDate.now());
        account.setEstimatedMonthlyIncome(new BigDecimal("5000.00"));
        account.setEstimatedMonthlyGoal(new BigDecimal("4500.00"));
        account.setStatus("on_track");
        return account;
    }

    private RetirementAccountDTO createTestAccountDTO() {
        RetirementAccountDTO dto = new RetirementAccountDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setAccountName("Updated 401(k) Account");
        dto.setAccountType("401k");
        dto.setCurrentBalance(new BigDecimal("120000.00"));
        dto.setAsOfDate(LocalDate.now());
        dto.setEstimatedMonthlyIncome(new BigDecimal("6000.00"));
        dto.setEstimatedMonthlyGoal(new BigDecimal("5500.00"));
        dto.setStatus("on_track");
        return dto;
    }
}
