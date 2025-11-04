package com.finova.user.service;

import com.finova.user.dto.UserProfileDTO;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserService
 * Tests user profile management operations
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
        testUser.setDateOfBirth(LocalDate.of(1985, 6, 15));
        testUser.setAnnualSalary(new BigDecimal("75000.00"));
        testUser.setEmploymentStatus("FULL_TIME");
        testUser.setYearsWithEmployer(5);
        testUser.setMaritalStatus("SINGLE");
        testUser.setRiskTolerance("MODERATE");

        testProfileDTO = new UserProfileDTO();
        testProfileDTO.setUsername("testuser");
        testProfileDTO.setFirstName("John");
        testProfileDTO.setLastName("Doe");
        testProfileDTO.setEmail("john.doe@example.com");
        testProfileDTO.setPhone("555-1234");
        testProfileDTO.setDateOfBirth(LocalDate.of(1985, 6, 15));
        testProfileDTO.setAnnualSalary(new BigDecimal("75000.00"));
        testProfileDTO.setEmploymentStatus("FULL_TIME");
        testProfileDTO.setYearsWithEmployer(5);
        testProfileDTO.setMaritalStatus("SINGLE");
        testProfileDTO.setRiskTolerance("MODERATE");
    }

    @Nested
    @DisplayName("Get User Profile Tests")
    class GetUserProfileTests {

        @Test
        @DisplayName("Should get user profile by ID successfully")
        void shouldGetUserProfileByIdSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserProfileDTO result = userService.getUserProfile(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
            
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void shouldThrowExceptionWhenUserNotFoundById() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserProfile(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");
            
            verify(userRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should get user profile by username successfully")
        void shouldGetUserProfileByUsernameSuccessfully() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserProfileDTO result = userService.getUserProfileByUsername("testuser");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getFirstName()).isEqualTo("John");
            assertThat(result.getLastName()).isEqualTo("Doe");
            
            verify(userRepository, times(1)).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should throw exception when user not found by username")
        void shouldThrowExceptionWhenUserNotFoundByUsername() {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserProfileByUsername("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with username: nonexistent");
            
            verify(userRepository, times(1)).findByUsername("nonexistent");
        }

        @Test
        @DisplayName("Should map all user fields to DTO correctly")
        void shouldMapAllUserFieldsToDtoCorrectly() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserProfileDTO result = userService.getUserProfile(1L);

            assertThat(result.getId()).isEqualTo(testUser.getId());
            assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
            assertThat(result.getFirstName()).isEqualTo(testUser.getFirstName());
            assertThat(result.getLastName()).isEqualTo(testUser.getLastName());
            assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(result.getPhone()).isEqualTo(testUser.getPhone());
            assertThat(result.getDateOfBirth()).isEqualTo(testUser.getDateOfBirth());
            assertThat(result.getAnnualSalary()).isEqualByComparingTo(testUser.getAnnualSalary());
            assertThat(result.getEmploymentStatus()).isEqualTo(testUser.getEmploymentStatus());
            assertThat(result.getYearsWithEmployer()).isEqualTo(testUser.getYearsWithEmployer());
            assertThat(result.getMaritalStatus()).isEqualTo(testUser.getMaritalStatus());
            assertThat(result.getRiskTolerance()).isEqualTo(testUser.getRiskTolerance());
        }
    }

    @Nested
    @DisplayName("Update User Profile Tests")
    class UpdateUserProfileTests {

        @Test
        @DisplayName("Should update user profile successfully")
        void shouldUpdateUserProfileSuccessfully() {
            UserProfileDTO updateDTO = new UserProfileDTO();
            updateDTO.setFirstName("Jane");
            updateDTO.setLastName("Smith");
            updateDTO.setEmail("jane.smith@example.com");
            updateDTO.setPhone("555-9876");
            updateDTO.setDateOfBirth(LocalDate.of(1990, 3, 20));
            updateDTO.setAnnualSalary(new BigDecimal("85000.00"));
            updateDTO.setEmploymentStatus("FULL_TIME");
            updateDTO.setYearsWithEmployer(3);
            updateDTO.setMaritalStatus("MARRIED");
            updateDTO.setRiskTolerance("AGGRESSIVE");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserProfileDTO result = userService.updateUserProfile(1L, updateDTO);

            assertThat(result).isNotNull();
            verify(userRepository, times(1)).findById(1L);
            verify(userRepository, times(1)).save(any(User.class));
            
            // Verify user was updated
            assertThat(testUser.getFirstName()).isEqualTo("Jane");
            assertThat(testUser.getLastName()).isEqualTo("Smith");
            assertThat(testUser.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(testUser.getPhone()).isEqualTo("555-9876");
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUserProfile(999L, testProfileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");
            
            verify(userRepository, times(1)).findById(999L);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should update financial profile fields")
        void shouldUpdateFinancialProfileFields() {
            UserProfileDTO updateDTO = new UserProfileDTO();
            updateDTO.setFirstName("John");
            updateDTO.setLastName("Doe");
            updateDTO.setEmail("john.doe@example.com");
            updateDTO.setAnnualSalary(new BigDecimal("95000.00"));
            updateDTO.setEmploymentStatus("FULL_TIME");
            updateDTO.setYearsWithEmployer(10);
            updateDTO.setRiskTolerance("CONSERVATIVE");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(1L, updateDTO);

            assertThat(testUser.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("95000.00"));
            assertThat(testUser.getYearsWithEmployer()).isEqualTo(10);
            assertThat(testUser.getRiskTolerance()).isEqualTo("CONSERVATIVE");
            
            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should update timestamp when profile is updated")
        void shouldUpdateTimestampWhenProfileIsUpdated() {
            LocalDateTime beforeUpdate = LocalDateTime.now().minusMinutes(5);
            testUser.setUpdatedAt(beforeUpdate);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(1L, testProfileDTO);

            assertThat(testUser.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should update retirement planning parameters")
        void shouldUpdateRetirementPlanningParameters() {
            UserProfileDTO updateDTO = new UserProfileDTO();
            updateDTO.setFirstName("John");
            updateDTO.setLastName("Doe");
            updateDTO.setEmail("john.doe@example.com");
            updateDTO.setDateOfBirth(LocalDate.of(1975, 1, 1));
            updateDTO.setAnnualSalary(new BigDecimal("120000.00"));
            updateDTO.setYearsWithEmployer(15);
            updateDTO.setMaritalStatus("MARRIED");
            updateDTO.setRiskTolerance("MODERATE");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(1L, updateDTO);

            // Verify retirement-relevant fields
            assertThat(testUser.getDateOfBirth()).isEqualTo(LocalDate.of(1975, 1, 1));
            assertThat(testUser.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("120000.00"));
            assertThat(testUser.getYearsWithEmployer()).isEqualTo(15);
            assertThat(testUser.getMaritalStatus()).isEqualTo("MARRIED");
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            userService.deleteUser(1L);

            verify(userRepository, times(1)).existsById(1L);
            verify(userRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            when(userRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 999");
            
            verify(userRepository, times(1)).existsById(999L);
            verify(userRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should verify user exists before deletion")
        void shouldVerifyUserExistsBeforeDeletion() {
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            userService.deleteUser(1L);

            verify(userRepository, times(1)).existsById(1L);
        }
    }

    @Nested
    @DisplayName("Financial Domain Tests")
    class FinancialDomainTests {

        @Test
        @DisplayName("Should handle high net worth individual profile")
        void shouldHandleHighNetWorthIndividualProfile() {
            User highNetWorthUser = new User("wealthy", "pass", "Rich", "Person", "rich@example.com");
            highNetWorthUser.setId(2L);
            highNetWorthUser.setAnnualSalary(new BigDecimal("500000.00"));
            highNetWorthUser.setEmploymentStatus("EXECUTIVE");
            highNetWorthUser.setYearsWithEmployer(20);
            highNetWorthUser.setRiskTolerance("AGGRESSIVE");

            when(userRepository.findById(2L)).thenReturn(Optional.of(highNetWorthUser));

            UserProfileDTO result = userService.getUserProfile(2L);

            assertThat(result.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("500000.00"));
            assertThat(result.getEmploymentStatus()).isEqualTo("EXECUTIVE");
            assertThat(result.getYearsWithEmployer()).isEqualTo(20);
        }

        @Test
        @DisplayName("Should handle near-retirement user profile")
        void shouldHandleNearRetirementUserProfile() {
            User nearRetirementUser = new User("retiring", "pass", "Senior", "Worker", "senior@example.com");
            nearRetirementUser.setId(3L);
            nearRetirementUser.setDateOfBirth(LocalDate.now().minusYears(62));
            nearRetirementUser.setAnnualSalary(new BigDecimal("90000.00"));
            nearRetirementUser.setYearsWithEmployer(30);
            nearRetirementUser.setRiskTolerance("CONSERVATIVE");

            when(userRepository.findById(3L)).thenReturn(Optional.of(nearRetirementUser));

            UserProfileDTO result = userService.getUserProfile(3L);

            assertThat(result.getDateOfBirth()).isEqualTo(nearRetirementUser.getDateOfBirth());
            assertThat(result.getYearsWithEmployer()).isEqualTo(30);
            assertThat(result.getRiskTolerance()).isEqualTo("CONSERVATIVE");
        }

        @Test
        @DisplayName("Should handle young professional profile")
        void shouldHandleYoungProfessionalProfile() {
            User youngProfessional = new User("young", "pass", "Young", "Professional", "young@example.com");
            youngProfessional.setId(4L);
            youngProfessional.setDateOfBirth(LocalDate.now().minusYears(28));
            youngProfessional.setAnnualSalary(new BigDecimal("65000.00"));
            youngProfessional.setYearsWithEmployer(3);
            youngProfessional.setRiskTolerance("AGGRESSIVE");

            when(userRepository.findById(4L)).thenReturn(Optional.of(youngProfessional));

            UserProfileDTO result = userService.getUserProfile(4L);

            assertThat(result.getYearsWithEmployer()).isEqualTo(3);
            assertThat(result.getRiskTolerance()).isEqualTo("AGGRESSIVE");
        }

        @Test
        @DisplayName("Should update marital status for tax planning")
        void shouldUpdateMaritalStatusForTaxPlanning() {
            UserProfileDTO updateDTO = new UserProfileDTO();
            updateDTO.setFirstName("John");
            updateDTO.setLastName("Doe");
            updateDTO.setEmail("john.doe@example.com");
            updateDTO.setMaritalStatus("MARRIED");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(1L, updateDTO);

            assertThat(testUser.getMaritalStatus()).isEqualTo("MARRIED");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null optional fields in profile")
        void shouldHandleNullOptionalFieldsInProfile() {
            User minimalUser = new User("minimal", "pass", "Min", "User", "min@example.com");
            minimalUser.setId(5L);
            // Leave optional fields null

            when(userRepository.findById(5L)).thenReturn(Optional.of(minimalUser));

            UserProfileDTO result = userService.getUserProfile(5L);

            assertThat(result).isNotNull();
            assertThat(result.getPhone()).isNull();
            assertThat(result.getDateOfBirth()).isNull();
            assertThat(result.getAnnualSalary()).isNull();
        }

        @Test
        @DisplayName("Should handle profile update with partial data")
        void shouldHandleProfileUpdateWithPartialData() {
            UserProfileDTO partialUpdate = new UserProfileDTO();
            partialUpdate.setFirstName("UpdatedFirst");
            partialUpdate.setLastName("UpdatedLast");
            partialUpdate.setEmail("updated@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.updateUserProfile(1L, partialUpdate);

            assertThat(testUser.getFirstName()).isEqualTo("UpdatedFirst");
            assertThat(testUser.getLastName()).isEqualTo("UpdatedLast");
        }
    }
}
