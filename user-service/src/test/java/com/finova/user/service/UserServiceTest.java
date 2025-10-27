package com.finova.user.service;

import com.finova.user.dto.UserProfileDTO;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserProfileDTO testUserProfileDTO;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUserProfileDTO = createTestUserProfileDTO();
    }

    @Test
    void getUserProfile_WhenUserExists_ShouldReturnUserProfileDTO() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserProfileDTO result = userService.getUserProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserProfile_WhenUserNotFound_ShouldThrowRuntimeException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfile(999L));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserProfileByUsername_WhenUserExists_ShouldReturnUserProfileDTO() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserProfileDTO result = userService.getUserProfileByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserProfileByUsername_WhenUserNotFound_ShouldThrowRuntimeException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfileByUsername("nonexistent"));
        
        assertEquals("User not found with username: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void updateUserProfile_WhenUserExists_ShouldUpdateAndReturnUserProfileDTO() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserProfileDTO result = userService.updateUserProfile(1L, testUserProfileDTO);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUserProfileDTO.getFirstName(), result.getFirstName());
        assertEquals(testUserProfileDTO.getLastName(), result.getLastName());
        assertEquals(testUserProfileDTO.getEmail(), result.getEmail());
        
        // Verify that user fields were updated
        assertEquals(testUserProfileDTO.getFirstName(), testUser.getFirstName());
        assertEquals(testUserProfileDTO.getLastName(), testUser.getLastName());
        assertEquals(testUserProfileDTO.getEmail(), testUser.getEmail());
        assertNotNull(testUser.getUpdatedAt());
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserProfile_WhenUserNotFound_ShouldThrowRuntimeException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUserProfile(999L, testUserProfileDTO));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowRuntimeException() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.deleteUser(999L));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void convertToDTO_ShouldMapAllFieldsCorrectly() {
        // Given
        User user = createTestUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("123-456-7890");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setAnnualSalary(new BigDecimal("75000"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(5);
        user.setMaritalStatus("SINGLE");
        user.setRiskTolerance("MODERATE");

        // When
        UserProfileDTO result = userService.getUserProfile(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Then
        result = userService.getUserProfile(1L);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhone(), result.getPhone());
        assertEquals(user.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(user.getAnnualSalary(), result.getAnnualSalary());
        assertEquals(user.getEmploymentStatus(), result.getEmploymentStatus());
        assertEquals(user.getYearsWithEmployer(), result.getYearsWithEmployer());
        assertEquals(user.getMaritalStatus(), result.getMaritalStatus());
        assertEquals(user.getRiskTolerance(), result.getRiskTolerance());
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhone("123-456-7890");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setAnnualSalary(new BigDecimal("75000"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(5);
        user.setMaritalStatus("SINGLE");
        user.setRiskTolerance("MODERATE");
        user.setEnabled(true);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserProfileDTO createTestUserProfileDTO() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setPhone("987-654-3210");
        dto.setDateOfBirth(LocalDate.of(1985, 5, 15));
        dto.setAnnualSalary(new BigDecimal("85000"));
        dto.setEmploymentStatus("FULL_TIME");
        dto.setYearsWithEmployer(8);
        dto.setMaritalStatus("MARRIED");
        dto.setRiskTolerance("AGGRESSIVE");
        return dto;
    }
}
