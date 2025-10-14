package com.finova.user.service;

import com.finova.user.dto.UserProfileDTO;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service class for User management operations
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get user profile by ID
     */
    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * Get user profile by username
     */
    public UserProfileDTO getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return convertToDTO(user);
    }

    /**
     * Update user profile
     */
    public UserProfileDTO updateUserProfile(Long id, UserProfileDTO profileDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update user fields
        user.setFirstName(profileDTO.getFirstName());
        user.setLastName(profileDTO.getLastName());
        user.setEmail(profileDTO.getEmail());
        user.setPhone(profileDTO.getPhone());
        user.setDateOfBirth(profileDTO.getDateOfBirth());
        user.setAnnualSalary(profileDTO.getAnnualSalary());
        user.setEmploymentStatus(profileDTO.getEmploymentStatus());
        user.setYearsWithEmployer(profileDTO.getYearsWithEmployer());
        user.setMaritalStatus(profileDTO.getMaritalStatus());
        user.setRiskTolerance(profileDTO.getRiskTolerance());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Convert User entity to UserProfileDTO
     */
    private UserProfileDTO convertToDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAnnualSalary(user.getAnnualSalary());
        dto.setEmploymentStatus(user.getEmploymentStatus());
        dto.setYearsWithEmployer(user.getYearsWithEmployer());
        dto.setMaritalStatus(user.getMaritalStatus());
        dto.setRiskTolerance(user.getRiskTolerance());
        return dto;
    }
}
