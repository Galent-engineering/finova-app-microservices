package com.finova.planning.controller;

import com.finova.planning.dto.RetirementPlanDTO;
import com.finova.planning.dto.SocialSecurityDTO;
import com.finova.planning.dto.InvestmentStrategyDTO;
import com.finova.planning.service.RetirementCalculatorService;
import com.finova.planning.service.SocialSecurityService;
import com.finova.planning.service.InvestmentStrategyService;
import com.finova.planning.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PlanningController
 */
@WebMvcTest(PlanningController.class)
class PlanningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetirementCalculatorService retirementCalculatorService;

    @MockBean
    private SocialSecurityService socialSecurityService;

    @MockBean
    private InvestmentStrategyService investmentStrategyService;

    @MockBean
    private AuthenticationService authenticationService;

    private RetirementPlanDTO testRetirementPlan;
    private SocialSecurityDTO testSocialSecurity;
    private InvestmentStrategyDTO testInvestmentStrategy;

    @BeforeEach
    void setUp() {
        // Setup test data
        testRetirementPlan = new RetirementPlanDTO(1L, 42, 65);
        testRetirementPlan.setProjectedBalance(new BigDecimal("500000"));
        testRetirementPlan.setProjectedMonthlyIncome(new BigDecimal("4000"));
        testRetirementPlan.setStatus("on_track");
        testRetirementPlan.setRecommendations("Great job!");

        testSocialSecurity = new SocialSecurityDTO();
        testSocialSecurity.setUserId(1L);
        testSocialSecurity.setBenefitAt67(new BigDecimal("2500"));
        testSocialSecurity.setFullRetirementAge(67);

        testInvestmentStrategy = new InvestmentStrategyDTO();
        testInvestmentStrategy.setUserId(1L);
        testInvestmentStrategy.setCurrentStrategy("Moderate Growth");
        testInvestmentStrategy.setRiskLevel("Moderate");
        testInvestmentStrategy.setStocksPercentage(65);
        testInvestmentStrategy.setBondsPercentage(30);
        testInvestmentStrategy.setCashPercentage(5);

        // Setup mocks
        when(authenticationService.validateUserAccess(1L)).thenReturn(true);
        when(retirementCalculatorService.calculateRetirementPlan(any(RetirementPlanDTO.class))).thenReturn(testRetirementPlan);
        when(socialSecurityService.calculateBenefits(any(SocialSecurityDTO.class))).thenReturn(testSocialSecurity);
        when(investmentStrategyService.generateStrategy(anyLong(), anyInt())).thenReturn(testInvestmentStrategy);
    }

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/planning/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Planning Service"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getRetirementPlan_WithValidUserId_ShouldReturnRetirementPlan() throws Exception {
        mockMvc.perform(get("/api/planning/retirement-plan/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.currentAge").value(42))
                .andExpect(jsonPath("$.retirementAge").value(65))
                .andExpect(jsonPath("$.projectedBalance").value(500000))
                .andExpect(jsonPath("$.projectedMonthlyIncome").value(4000))
                .andExpect(jsonPath("$.status").value("on_track"));
    }

    @Test
    void getRetirementPlan_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/retirement-plan/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void calculateRetirementPlan_WithValidData_ShouldReturnCalculatedPlan() throws Exception {
        String requestBody = """
                {
                    "userId": 1,
                    "currentAge": 40,
                    "retirementAge": 65,
                    "currentSavings": 100000,
                    "monthlyContribution": 1000,
                    "expectedReturnRate": 7.0
                }
                """;

        mockMvc.perform(post("/api/planning/retirement-plan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.projectedBalance").value(500000))
                .andExpect(jsonPath("$.status").value("on_track"));
    }

    @Test
    void calculateRetirementPlan_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        String requestBody = """
                {
                    "userId": 1,
                    "currentAge": 40,
                    "retirementAge": 65
                }
                """;

        mockMvc.perform(post("/api/planning/retirement-plan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void getWhatIfScenarios_WithValidUserId_ShouldReturnScenarios() throws Exception {
        mockMvc.perform(get("/api/planning/scenarios/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current").exists())
                .andExpect(jsonPath("$.scenarioA").exists())
                .andExpect(jsonPath("$.scenarioA.description").value("Increase contributions by 20%"))
                .andExpect(jsonPath("$.scenarioB").exists())
                .andExpect(jsonPath("$.scenarioB.description").value("Retire at age 63"))
                .andExpect(jsonPath("$.scenarioC").exists())
                .andExpect(jsonPath("$.scenarioC.description").value("Retire at age 67"));
    }

    @Test
    void getWhatIfScenarios_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/scenarios/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSocialSecurityBenefits_WithValidUserId_ShouldReturnBenefits() throws Exception {
        mockMvc.perform(get("/api/planning/social-security/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.benefitAt67").value(2500))
                .andExpect(jsonPath("$.fullRetirementAge").value(67));
    }

    @Test
    void getSocialSecurityBenefits_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/social-security/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void calculateSocialSecurityBenefits_WithValidData_ShouldReturnCalculatedBenefits() throws Exception {
        String requestBody = """
                {
                    "userId": 1,
                    "currentSalary": 75000,
                    "currentAge": 45
                }
                """;

        mockMvc.perform(post("/api/planning/social-security/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.benefitAt67").value(2500));
    }

    @Test
    void calculateSocialSecurityBenefits_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        String requestBody = """
                {
                    "userId": 1,
                    "currentSalary": 75000
                }
                """;

        mockMvc.perform(post("/api/planning/social-security/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void getInvestmentStrategy_WithValidUserId_ShouldReturnStrategy() throws Exception {
        mockMvc.perform(get("/api/planning/investment-strategy/{userId}?age=45", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.currentStrategy").value("Moderate Growth"))
                .andExpect(jsonPath("$.riskLevel").value("Moderate"))
                .andExpect(jsonPath("$.stocksPercentage").value(65))
                .andExpect(jsonPath("$.bondsPercentage").value(30))
                .andExpect(jsonPath("$.cashPercentage").value(5));
    }

    @Test
    void getInvestmentStrategy_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/investment-strategy/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPlanningTools_WithValidUserId_ShouldReturnTools() throws Exception {
        mockMvc.perform(get("/api/planning/tools/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.retirementPlanner").exists())
                .andExpect(jsonPath("$.retirementPlanner.title").value("Retirement Planner"))
                .andExpect(jsonPath("$.socialSecurity").exists())
                .andExpect(jsonPath("$.socialSecurity.title").value("Social Security Estimator"))
                .andExpect(jsonPath("$.investmentStrategy").exists())
                .andExpect(jsonPath("$.investmentStrategy.title").value("Investment Strategy"))
                .andExpect(jsonPath("$.scenarios").exists())
                .andExpect(jsonPath("$.scenarios.title").value("What-If Scenarios"));
    }

    @Test
    void getPlanningTools_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/tools/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPlanningDashboard_WithValidUserId_ShouldReturnDashboard() throws Exception {
        mockMvc.perform(get("/api/planning/dashboard/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.retirementPlan").exists())
                .andExpect(jsonPath("$.socialSecurity").exists())
                .andExpect(jsonPath("$.investmentStrategy").exists())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.summary.overallStatus").value("on_track"));
    }

    @Test
    void getPlanningDashboard_WithInvalidUserAccess_ShouldReturnForbidden() throws Exception {
        when(authenticationService.validateUserAccess(1L)).thenReturn(false);

        mockMvc.perform(get("/api/planning/dashboard/{userId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void calculateRetirementPlan_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/planning/retirement-plan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateSocialSecurityBenefits_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/planning/social-security/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
