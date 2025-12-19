package com.finova.account.controller;

import com.finova.account.dto.ContributionDTO;
import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.dto.RetirementAccountDTO;
import com.finova.account.service.ContributionService;
import com.finova.account.service.IncomeSourceService;
import com.finova.account.service.RetirementAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountController
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private RetirementAccountService retirementAccountService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private IncomeSourceService incomeSourceService;

    @InjectMocks
    private AccountController accountController;

    private RetirementAccountDTO testAccountDTO;
    private ContributionDTO testContributionDTO;
    private IncomeSourceDTO testIncomeSourceDTO;

    @BeforeEach
    void setUp() {
        testAccountDTO = createTestAccountDTO();
        testContributionDTO = createTestContributionDTO();
        testIncomeSourceDTO = createTestIncomeSourceDTO();
    }

    @Test
    void healthCheck_ShouldReturnOkResponse() {
        // When
        ResponseEntity<Map<String, String>> response = accountController.healthCheck();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("Account Service", response.getBody().get("service"));
    }

    @Test
    void getAccountsByUserId_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        List<RetirementAccountDTO> accounts = List.of(testAccountDTO);
        when(retirementAccountService.getAccountsByUserId(1L)).thenReturn(accounts);

        // When
        ResponseEntity<List<RetirementAccountDTO>> response = accountController.getAccountsByUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
        verify(retirementAccountService).getAccountsByUserId(1L);
    }

    @Test
    void getPrimaryAccount_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        when(retirementAccountService.getPrimaryAccountByUserId(1L)).thenReturn(testAccountDTO);

        // When
        ResponseEntity<RetirementAccountDTO> response = accountController.getPrimaryAccount(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAccountDTO, response.getBody());
        verify(retirementAccountService).getPrimaryAccountByUserId(1L);
    }

    @Test
    void getAccountById_WithValidAccountId_ShouldReturnOkResponse() {
        // Given
        when(retirementAccountService.getAccountById(1L)).thenReturn(testAccountDTO);

        // When
        ResponseEntity<RetirementAccountDTO> response = accountController.getAccountById(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAccountDTO, response.getBody());
        verify(retirementAccountService).getAccountById(1L);
    }

    @Test
    void createAccount_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(retirementAccountService.createAccount(testAccountDTO)).thenReturn(testAccountDTO);

        // When
        ResponseEntity<RetirementAccountDTO> response = accountController.createAccount(testAccountDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAccountDTO, response.getBody());
        verify(retirementAccountService).createAccount(testAccountDTO);
    }

    @Test
    void updateAccount_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(retirementAccountService.updateAccount(1L, testAccountDTO)).thenReturn(testAccountDTO);

        // When
        ResponseEntity<RetirementAccountDTO> response = accountController.updateAccount(1L, testAccountDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAccountDTO, response.getBody());
        verify(retirementAccountService).updateAccount(1L, testAccountDTO);
    }

    @Test
    void deleteAccount_WithValidAccountId_ShouldReturnNoContentResponse() {
        // Given
        doNothing().when(retirementAccountService).deleteAccount(1L);

        // When
        ResponseEntity<Void> response = accountController.deleteAccount(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(retirementAccountService).deleteAccount(1L);
    }

    @Test
    void getContributionsByUserId_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        List<ContributionDTO> contributions = List.of(testContributionDTO);
        when(contributionService.getContributionsByUserId(1L)).thenReturn(contributions);

        // When
        ResponseEntity<List<ContributionDTO>> response = accountController.getContributionsByUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contributions, response.getBody());
        verify(contributionService).getContributionsByUserId(1L);
    }

    @Test
    void getContributionsByAccountId_WithValidAccountId_ShouldReturnOkResponse() {
        // Given
        List<ContributionDTO> contributions = List.of(testContributionDTO);
        when(contributionService.getContributionsByAccountId(1L)).thenReturn(contributions);

        // When
        ResponseEntity<List<ContributionDTO>> response = accountController.getContributionsByAccountId(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contributions, response.getBody());
        verify(contributionService).getContributionsByAccountId(1L);
    }

    @Test
    void getContributionsSummary_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        List<ContributionDTO> contributions = List.of(testContributionDTO);
        when(contributionService.getContributionsByUserId(1L)).thenReturn(contributions);

        // When
        ResponseEntity<Map<String, Object>> response = accountController.getContributionsSummary(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("contributions"));
        assertTrue(response.getBody().containsKey("totalMonthly"));
        assertTrue(response.getBody().containsKey("totalAnnual"));
        verify(contributionService).getContributionsByUserId(1L);
    }

    @Test
    void createContribution_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(contributionService.createContribution(testContributionDTO)).thenReturn(testContributionDTO);

        // When
        ResponseEntity<ContributionDTO> response = accountController.createContribution(testContributionDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testContributionDTO, response.getBody());
        verify(contributionService).createContribution(testContributionDTO);
    }

    @Test
    void updateContribution_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(contributionService.updateContribution(1L, testContributionDTO)).thenReturn(testContributionDTO);

        // When
        ResponseEntity<ContributionDTO> response = accountController.updateContribution(1L, testContributionDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testContributionDTO, response.getBody());
        verify(contributionService).updateContribution(1L, testContributionDTO);
    }

    @Test
    void updateContributions_WithValidData_ShouldReturnOkResponse() {
        // Given
        List<ContributionDTO> contributions = List.of(testContributionDTO);
        when(contributionService.updateContributions(1L, contributions)).thenReturn(contributions);

        // When
        ResponseEntity<List<ContributionDTO>> response = accountController.updateContributions(1L, contributions);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contributions, response.getBody());
        verify(contributionService).updateContributions(1L, contributions);
    }

    @Test
    void deleteContribution_WithValidContributionId_ShouldReturnNoContentResponse() {
        // Given
        doNothing().when(contributionService).deleteContribution(1L);

        // When
        ResponseEntity<Void> response = accountController.deleteContribution(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(contributionService).deleteContribution(1L);
    }

    @Test
    void getIncomeSourcesByUserId_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        List<IncomeSourceDTO> incomeSources = List.of(testIncomeSourceDTO);
        when(incomeSourceService.getIncomeSourcesByUserId(1L)).thenReturn(incomeSources);

        // When
        ResponseEntity<List<IncomeSourceDTO>> response = accountController.getIncomeSourcesByUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(incomeSources, response.getBody());
        verify(incomeSourceService).getIncomeSourcesByUserId(1L);
    }

    @Test
    void getIncomeSourcesSummary_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        List<IncomeSourceDTO> incomeSources = List.of(testIncomeSourceDTO);
        when(incomeSourceService.getIncomeSourcesByUserId(1L)).thenReturn(incomeSources);

        // When
        ResponseEntity<Map<String, Object>> response = accountController.getIncomeSourcesSummary(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("incomeSources"));
        assertTrue(response.getBody().containsKey("totalProjectedIncome"));
        assertTrue(response.getBody().containsKey("totalCurrentBalance"));
        verify(incomeSourceService).getIncomeSourcesByUserId(1L);
    }

    @Test
    void createIncomeSource_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(incomeSourceService.createIncomeSource(testIncomeSourceDTO)).thenReturn(testIncomeSourceDTO);

        // When
        ResponseEntity<IncomeSourceDTO> response = accountController.createIncomeSource(testIncomeSourceDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testIncomeSourceDTO, response.getBody());
        verify(incomeSourceService).createIncomeSource(testIncomeSourceDTO);
    }

    @Test
    void updateIncomeSource_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(incomeSourceService.updateIncomeSource(1L, testIncomeSourceDTO)).thenReturn(testIncomeSourceDTO);

        // When
        ResponseEntity<IncomeSourceDTO> response = accountController.updateIncomeSource(1L, testIncomeSourceDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testIncomeSourceDTO, response.getBody());
        verify(incomeSourceService).updateIncomeSource(1L, testIncomeSourceDTO);
    }

    @Test
    void deleteIncomeSource_WithValidIncomeSourceId_ShouldReturnNoContentResponse() {
        // Given
        doNothing().when(incomeSourceService).deleteIncomeSource(1L);

        // When
        ResponseEntity<Void> response = accountController.deleteIncomeSource(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(incomeSourceService).deleteIncomeSource(1L);
    }

    @Test
    void getDashboardData_WithValidUserId_ShouldReturnOkResponse() {
        // Given
        when(retirementAccountService.getPrimaryAccountByUserId(1L)).thenReturn(testAccountDTO);
        when(contributionService.getContributionsByUserId(1L)).thenReturn(List.of(testContributionDTO));
        when(incomeSourceService.getIncomeSourcesByUserId(1L)).thenReturn(List.of(testIncomeSourceDTO));

        // When
        ResponseEntity<Map<String, Object>> response = accountController.getDashboardData(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("primaryAccount"));
        assertTrue(response.getBody().containsKey("contributions"));
        assertTrue(response.getBody().containsKey("incomeSources"));
        verify(retirementAccountService).getPrimaryAccountByUserId(1L);
        verify(contributionService).getContributionsByUserId(1L);
        verify(incomeSourceService).getIncomeSourcesByUserId(1L);
    }

    private RetirementAccountDTO createTestAccountDTO() {
        RetirementAccountDTO dto = new RetirementAccountDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setAccountName("Test 401(k) Account");
        dto.setAccountType("401k");
        dto.setCurrentBalance(new BigDecimal("100000.00"));
        dto.setAsOfDate(LocalDate.now());
        dto.setEstimatedMonthlyIncome(new BigDecimal("5000.00"));
        dto.setEstimatedMonthlyGoal(new BigDecimal("4500.00"));
        dto.setStatus("on_track");
        return dto;
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
