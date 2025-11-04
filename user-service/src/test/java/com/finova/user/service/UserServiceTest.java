package com.finova.user.service;

import com.finova.user.dto.UserProfileDTO;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserProfileDTO testProfileDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123", "John", "Doe", "john.doe@example.com");
        testUser.setId(1L);
        testUser.setPhone("555-1234");
        testUser.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testUser.setAnnualSalary(new BigDecimal("75000.00"));
        testUser.setEmploymentStatus("Full-Time");
        testUser.setYearsWithEmployer(5);
        testUser.setMaritalStatus("Single");
        testUser.setRiskTolerance("Moderate");

        testProfileDTO = new UserProfileDTO();
        testProfileDTO.setId(1L);
        testProfileDTO.setUsername("testuser");
        testProfileDTO.setFirstName("John");
        testProfileDTO.setLastName("Doe");
        testProfileDTO.setEmail("john.doe@example.com");
        testProfileDTO.setPhone("555-1234");
        testProfileDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testProfileDTO.setAnnualSalary(new BigDecimal("75000.00"));
        testProfileDTO.setEmploymentStatus("Full-Time");
        testProfileDTO.setYearsWithEmployer(5);
        testProfileDTO.setMaritalStatus("Single");
        testProfileDTO.setRiskTolerance("Moderate");
    }

    @Test
    @DisplayName("Should get user profile by ID successfully")
    void testGetUserProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileDTO result = userService.getUserProfile(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertEquals(testUser.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(testUser.getAnnualSalary(), result.getAnnualSalary());
        assertEquals(testUser.getEmploymentStatus(), result.getEmploymentStatus());
        assertEquals(testUser.getYearsWithEmployer(), result.getYearsWithEmployer());
        assertEquals(testUser.getMaritalStatus(), result.getMaritalStatus());
        assertEquals(testUser.getRiskTolerance(), result.getRiskTolerance());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void testGetUserProfile_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfile(999L));

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user profile by username successfully")
    void testGetUserProfileByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserProfileDTO result = userService.getUserProfileByUsername("testuser");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void testGetUserProfileByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfileByUsername("nonexistent"));

        assertEquals("User not found with username: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void testUpdateUserProfile_Success() {
        UserProfileDTO updateDTO = new UserProfileDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@example.com");
        updateDTO.setPhone("555-5678");
        updateDTO.setDateOfBirth(LocalDate.of(1990, 8, 20));
        updateDTO.setAnnualSalary(new BigDecimal("85000.00"));
        updateDTO.setEmploymentStatus("Part-Time");
        updateDTO.setYearsWithEmployer(3);
        updateDTO.setMaritalStatus("Married");
        updateDTO.setRiskTolerance("Conservative");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserProfileDTO result = userService.updateUserProfile(1L, updateDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that user fields were updated
        assertEquals("Jane", testUser.getFirstName());
        assertEquals("Smith", testUser.getLastName());
        assertEquals("jane.smith@example.com", testUser.getEmail());
        assertEquals("555-5678", testUser.getPhone());
        assertEquals(LocalDate.of(1990, 8, 20), testUser.getDateOfBirth());
        assertEquals(new BigDecimal("85000.00"), testUser.getAnnualSalary());
        assertEquals("Part-Time", testUser.getEmploymentStatus());
        assertEquals(3, testUser.getYearsWithEmployer());
        assertEquals("Married", testUser.getMaritalStatus());
        assertEquals("Conservative", testUser.getRiskTolerance());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserProfile_UserNotFound() {
        UserProfileDTO updateDTO = new UserProfileDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUserProfile(999L, updateDTO));

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.deleteUser(999L));

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should handle user with retirement planning data")
    void testUserProfileWithRetirementData() {
        User retirementUser = new User("retiree", "pass123", "Senior", "Citizen", "senior@example.com");
        retirementUser.setId(2L);
        retirementUser.setDateOfBirth(LocalDate.of(1960, 1, 1));
        retirementUser.setAnnualSalary(new BigDecimal("120000.00"));
        retirementUser.setEmploymentStatus("Full-Time");
        retirementUser.setYearsWithEmployer(25);
        retirementUser.setRiskTolerance("Conservative");

        when(userRepository.findById(2L)).thenReturn(Optional.of(retirementUser));

        UserProfileDTO result = userService.getUserProfile(2L);

        assertNotNull(result);
        assertEquals(new BigDecimal("120000.00"), result.getAnnualSalary());
        assertEquals(25, result.getYearsWithEmployer());
        assertEquals("Conservative", result.getRiskTolerance());
        assertNotNull(result.getDateOfBirth());
    }

    @Test
    @DisplayName("Should update only financial fields")
    void testUpdateFinancialFieldsOnly() {
        UserProfileDTO updateDTO = new UserProfileDTO();
        updateDTO.setFirstName(testUser.getFirstName());
        updateDTO.setLastName(testUser.getLastName());
        updateDTO.setEmail(testUser.getEmail());
        updateDTO.setPhone(testUser.getPhone());
        updateDTO.setDateOfBirth(testUser.getDateOfBirth());
        updateDTO.setAnnualSalary(new BigDecimal("95000.00")); // Updated
        updateDTO.setEmploymentStatus("Full-Time");
        updateDTO.setYearsWithEmployer(6); // Updated
        updateDTO.setMaritalStatus(testUser.getMaritalStatus());
        updateDTO.setRiskTolerance("Aggressive"); // Updated

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserProfileDTO result = userService.updateUserProfile(1L, updateDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("95000.00"), testUser.getAnnualSalary());
        assertEquals(6, testUser.getYearsWithEmployer());
        assertEquals("Aggressive", testUser.getRiskTolerance());
    }

    @Test
    @DisplayName("Should convert User entity to UserProfileDTO correctly")
    void testConvertToDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserProfileDTO result = userService.getUserProfile(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertEquals(testUser.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(testUser.getAnnualSalary(), result.getAnnualSalary());
        assertEquals(testUser.getEmploymentStatus(), result.getEmploymentStatus());
        assertEquals(testUser.getYearsWithEmployer(), result.getYearsWithEmployer());
        assertEquals(testUser.getMaritalStatus(), result.getMaritalStatus());
        assertEquals(testUser.getRiskTolerance(), result.getRiskTolerance());
    }
}
