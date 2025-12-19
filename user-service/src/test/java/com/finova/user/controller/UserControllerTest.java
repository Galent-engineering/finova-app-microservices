package com.finova.user.controller;

import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.dto.UserProfileDTO;
import com.finova.user.service.AuthService;
import com.finova.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserController userController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private UserProfileDTO userProfileDTO;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = createLoginRequest();
        registerRequest = createRegisterRequest();
        userProfileDTO = createUserProfileDTO();
        loginResponse = createLoginResponse();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnOkResponse() {
        // Given
        when(authService.authenticate(loginRequest)).thenReturn(loginResponse);

        // When
        ResponseEntity<LoginResponse> response = userController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
        verify(authService).authenticate(loginRequest);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnOkResponseWithError() {
        // Given
        LoginResponse errorResponse = new LoginResponse("Invalid credentials");
        when(authService.authenticate(loginRequest)).thenReturn(errorResponse);

        // When
        ResponseEntity<LoginResponse> response = userController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(errorResponse, response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(authService).authenticate(loginRequest);
    }

    @Test
    void register_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(authService.register(registerRequest)).thenReturn(loginResponse);

        // When
        ResponseEntity<LoginResponse> response = userController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
        verify(authService).register(registerRequest);
    }

    @Test
    void register_WithExistingUsername_ShouldReturnOkResponseWithError() {
        // Given
        LoginResponse errorResponse = new LoginResponse("Username already exists");
        when(authService.register(registerRequest)).thenReturn(errorResponse);

        // When
        ResponseEntity<LoginResponse> response = userController.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(errorResponse, response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(authService).register(registerRequest);
    }

    @Test
    void getUserProfile_WithValidId_ShouldReturnOkResponse() {
        // Given
        when(userService.getUserProfile(1L)).thenReturn(userProfileDTO);

        // When
        ResponseEntity<UserProfileDTO> response = userController.getUserProfile(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDTO, response.getBody());
        verify(userService).getUserProfile(1L);
    }

    @Test
    void getUserProfile_WithInvalidId_ShouldThrowException() {
        // Given
        when(userService.getUserProfile(999L))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userController.getUserProfile(999L));
        verify(userService).getUserProfile(999L);
    }

    @Test
    void getUserProfileByUsername_WithValidUsername_ShouldReturnOkResponse() {
        // Given
        when(userService.getUserProfileByUsername("testuser")).thenReturn(userProfileDTO);

        // When
        ResponseEntity<UserProfileDTO> response = userController.getUserProfileByUsername("testuser");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDTO, response.getBody());
        verify(userService).getUserProfileByUsername("testuser");
    }

    @Test
    void getUserProfileByUsername_WithInvalidUsername_ShouldThrowException() {
        // Given
        when(userService.getUserProfileByUsername("nonexistent"))
                .thenThrow(new RuntimeException("User not found with username: nonexistent"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userController.getUserProfileByUsername("nonexistent"));
        verify(userService).getUserProfileByUsername("nonexistent");
    }

    @Test
    void updateUserProfile_WithValidData_ShouldReturnOkResponse() {
        // Given
        when(userService.updateUserProfile(1L, userProfileDTO)).thenReturn(userProfileDTO);

        // When
        ResponseEntity<UserProfileDTO> response = userController.updateUserProfile(1L, userProfileDTO);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDTO, response.getBody());
        verify(userService).updateUserProfile(1L, userProfileDTO);
    }

    @Test
    void updateUserProfile_WithInvalidId_ShouldThrowException() {
        // Given
        when(userService.updateUserProfile(999L, userProfileDTO))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userController.updateUserProfile(999L, userProfileDTO));
        verify(userService).updateUserProfile(999L, userProfileDTO);
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnNoContentResponse() {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        // When & Then
        assertThrows(RuntimeException.class, () -> userController.deleteUser(999L));
        verify(userService).deleteUser(999L);
    }

    @Test
    void healthCheck_ShouldReturnOkResponse() {
        // When
        ResponseEntity<String> response = userController.healthCheck();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Service is running", response.getBody());
    }

    private LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        return request;
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        return request;
    }

    private UserProfileDTO createUserProfileDTO() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("123-456-7890");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAnnualSalary(new BigDecimal("75000"));
        dto.setEmploymentStatus("FULL_TIME");
        dto.setYearsWithEmployer(5);
        dto.setMaritalStatus("SINGLE");
        dto.setRiskTolerance("MODERATE");
        return dto;
    }

    private LoginResponse createLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken("jwt-token");
        response.setUserId(1L);
        response.setUsername("testuser");
        response.setEmail("test@example.com");
        return response;
    }
}
