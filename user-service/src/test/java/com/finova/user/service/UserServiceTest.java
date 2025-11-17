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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * Tests user profile management operations with mocked repository
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
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("+1234567890");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setAnnualSalary(new BigDecimal("75000.00"));
        testUser.setEmploymentStatus("Full-time");
        testUser.setYearsWithEmployer(5);
        testUser.setMaritalStatus("Single");
        testUser.setRiskTolerance("Moderate");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testProfileDTO = new UserProfileDTO();
        testProfileDTO.setUsername("testuser");
        testProfileDTO.setFirstName("Jane");
        testProfileDTO.setLastName("Smith");
        testProfileDTO.setEmail("jane.smith@example.com");
        testProfileDTO.setPhone("+9876543210");
        testProfileDTO.setDateOfBirth(LocalDate.of(1992, 5, 15));
        testProfileDTO.setAnnualSalary(new BigDecimal("85000.00"));
        testProfileDTO.setEmploymentStatus("Self-employed");
        testProfileDTO.setYearsWithEmployer(3);
        testProfileDTO.setMaritalStatus("Married");
        testProfileDTO.setRiskTolerance("Aggressive");
    }

    @Test
    @DisplayName("Should get user profile by ID successfully")
    void testGetUserProfileById() {
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
    void testGetUserProfileByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfile(999L));

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user profile by username successfully")
    void testGetUserProfileByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserProfileDTO result = userService.getUserProfileByUsername("testuser");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void testGetUserProfileByUsernameNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUserProfileByUsername("nonexistent"));

        assertTrue(exception.getMessage().contains("User not found with username: nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void testUpdateUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserProfileDTO result = userService.updateUserProfile(1L, testProfileDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that user fields were updated
        assertEquals(testProfileDTO.getFirstName(), testUser.getFirstName());
        assertEquals(testProfileDTO.getLastName(), testUser.getLastName());
        assertEquals(testProfileDTO.getEmail(), testUser.getEmail());
        assertEquals(testProfileDTO.getPhone(), testUser.getPhone());
        assertEquals(testProfileDTO.getDateOfBirth(), testUser.getDateOfBirth());
        assertEquals(testProfileDTO.getAnnualSalary(), testUser.getAnnualSalary());
        assertEquals(testProfileDTO.getEmploymentStatus(), testUser.getEmploymentStatus());
        assertEquals(testProfileDTO.getYearsWithEmployer(), testUser.getYearsWithEmployer());
        assertEquals(testProfileDTO.getMaritalStatus(), testUser.getMaritalStatus());
        assertEquals(testProfileDTO.getRiskTolerance(), testUser.getRiskTolerance());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateUserProfileNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUserProfile(999L, testProfileDTO));

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user profile with null optional fields")
    void testUpdateUserProfileWithNullFields() {
        testProfileDTO.setPhone(null);
        testProfileDTO.setDateOfBirth(null);
        testProfileDTO.setAnnualSalary(null);
        testProfileDTO.setEmploymentStatus(null);
        testProfileDTO.setYearsWithEmployer(null);
        testProfileDTO.setMaritalStatus(null);
        testProfileDTO.setRiskTolerance(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserProfileDTO result = userService.updateUserProfile(1L, testProfileDTO);

        assertNotNull(result);
        assertNull(testUser.getPhone());
        assertNull(testUser.getDateOfBirth());
        assertNull(testUser.getAnnualSalary());
        assertNull(testUser.getEmploymentStatus());
        assertNull(testUser.getYearsWithEmployer());
        assertNull(testUser.getMaritalStatus());
        assertNull(testUser.getRiskTolerance());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.deleteUser(999L));

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
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

    @Test
    @DisplayName("Should handle user with minimal required fields")
    void testGetUserProfileWithMinimalFields() {
        User minimalUser = new User();
        minimalUser.setId(2L);
        minimalUser.setUsername("minimal");
        minimalUser.setPassword("password");
        minimalUser.setFirstName("Min");
        minimalUser.setLastName("User");
        minimalUser.setEmail("min@example.com");

        when(userRepository.findById(2L)).thenReturn(Optional.of(minimalUser));

        UserProfileDTO result = userService.getUserProfile(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("minimal", result.getUsername());
        assertEquals("Min", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("min@example.com", result.getEmail());
        assertNull(result.getPhone());
        assertNull(result.getDateOfBirth());
        assertNull(result.getAnnualSalary());
    }

    @Test
    @DisplayName("Should update timestamp when updating profile")
    void testUpdateProfileUpdatesTimestamp() {
        LocalDateTime originalUpdatedAt = testUser.getUpdatedAt();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        userService.updateUserProfile(1L, testProfileDTO);

        assertNotEquals(originalUpdatedAt, testUser.getUpdatedAt());
        assertTrue(testUser.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   testUser.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}
