package com.finova.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.dto.UserProfileDTO;
import com.finova.user.service.AuthService;
import com.finova.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 * Tests REST endpoints with mocked services
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RegisterRequest registerRequest;
    private UserProfileDTO userProfileDTO;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "password123");
        
        loginResponse = new LoginResponse();
        loginResponse.setToken("mock-jwt-token");
        loginResponse.setId(1L);
        loginResponse.setUsername("testuser");
        loginResponse.setEmail("test@example.com");
        loginResponse.setMessage("Login successful");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");

        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(1L);
        userProfileDTO.setUsername("testuser");
        userProfileDTO.setFirstName("John");
        userProfileDTO.setLastName("Doe");
        userProfileDTO.setEmail("john.doe@example.com");
        userProfileDTO.setPhone("+1234567890");
        userProfileDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userProfileDTO.setAnnualSalary(new BigDecimal("75000.00"));
        userProfileDTO.setEmploymentStatus("Full-time");
        userProfileDTO.setYearsWithEmployer(5);
        userProfileDTO.setMaritalStatus("Single");
        userProfileDTO.setRiskTolerance("Moderate");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should login successfully")
    void testLoginSuccess() throws Exception {
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should handle invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        LoginResponse errorResponse = new LoginResponse("Invalid username or password");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(errorResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.token").doesNotExist());

        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register successfully")
    void testRegisterSuccess() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should handle username already exists")
    void testRegisterUsernameExists() throws Exception {
        LoginResponse errorResponse = new LoginResponse("Username already exists");
        when(authService.register(any(RegisterRequest.class))).thenReturn(errorResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username already exists"))
                .andExpect(jsonPath("$.token").doesNotExist());

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should get user profile by ID")
    void testGetUserProfileById() throws Exception {
        when(userService.getUserProfile(1L)).thenReturn(userProfileDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"))
                .andExpect(jsonPath("$.annualSalary").value(75000.00))
                .andExpect(jsonPath("$.employmentStatus").value("Full-time"))
                .andExpect(jsonPath("$.yearsWithEmployer").value(5))
                .andExpect(jsonPath("$.maritalStatus").value("Single"))
                .andExpect(jsonPath("$.riskTolerance").value("Moderate"));

        verify(userService, times(1)).getUserProfile(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should handle user not found")
    void testGetUserProfileByIdNotFound() throws Exception {
        when(userService.getUserProfile(anyLong()))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserProfile(999L);
    }

    @Test
    @DisplayName("GET /api/users/profile/{username} - Should get user profile by username")
    void testGetUserProfileByUsername() throws Exception {
        when(userService.getUserProfileByUsername("testuser")).thenReturn(userProfileDTO);

        mockMvc.perform(get("/api/users/profile/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).getUserProfileByUsername("testuser");
    }

    @Test
    @DisplayName("GET /api/users/profile/{username} - Should handle user not found by username")
    void testGetUserProfileByUsernameNotFound() throws Exception {
        when(userService.getUserProfileByUsername(anyString()))
                .thenThrow(new RuntimeException("User not found with username: nonexistent"));

        mockMvc.perform(get("/api/users/profile/nonexistent"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserProfileByUsername("nonexistent");
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user profile")
    void testUpdateUserProfile() throws Exception {
        UserProfileDTO updatedProfile = new UserProfileDTO();
        updatedProfile.setId(1L);
        updatedProfile.setUsername("testuser");
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Smith");
        updatedProfile.setEmail("jane.smith@example.com");

        when(userService.updateUserProfile(eq(1L), any(UserProfileDTO.class)))
                .thenReturn(updatedProfile);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        verify(userService, times(1)).updateUserProfile(eq(1L), any(UserProfileDTO.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should handle update for non-existent user")
    void testUpdateUserProfileNotFound() throws Exception {
        when(userService.updateUserProfile(anyLong(), any(UserProfileDTO.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileDTO)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).updateUserProfile(eq(999L), any(UserProfileDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user")
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should handle delete for non-existent user")
    void testDeleteUserNotFound() throws Exception {
        doThrow(new RuntimeException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    @DisplayName("GET /api/users/health - Should return health check status")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is running"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should validate required fields")
    void testLoginValidation() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should validate required fields")
    void testRegisterValidation() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("ab"); // Too short
        invalidRequest.setPassword("123"); // Too short
        invalidRequest.setFirstName("");
        invalidRequest.setLastName("");
        invalidRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should validate profile update fields")
    void testUpdateProfileValidation() throws Exception {
        UserProfileDTO invalidProfile = new UserProfileDTO();
        invalidProfile.setUsername("ab"); // Too short
        invalidProfile.setFirstName("");
        invalidProfile.setLastName("");
        invalidProfile.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProfile)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUserProfile(anyLong(), any(UserProfileDTO.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON in request body")
    void testMalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should handle missing Content-Type header")
    void testMissingContentType() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());

        verify(authService, never()).authenticate(any(LoginRequest.class));
    }
}
