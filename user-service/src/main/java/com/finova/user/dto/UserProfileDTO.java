package com.finova.user.dto;

import com.finova.user.validation.ValidRetirementAge;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * DTO for user profile information
 */
@ValidRetirementAge
public class UserProfileDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be a valid international format")
    private String phone;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @DecimalMin(value = "0.0", message = "Annual salary must be positive or zero")
    @DecimalMax(value = "9999999.99", message = "Annual salary cannot exceed $9,999,999.99")
    private BigDecimal annualSalary;
    
    @Pattern(regexp = "^(EMPLOYED|SELF_EMPLOYED|UNEMPLOYED|RETIRED|STUDENT)$", 
             message = "Employment status must be one of: EMPLOYED, SELF_EMPLOYED, UNEMPLOYED, RETIRED, STUDENT")
    private String employmentStatus;
    
    @Min(value = 0, message = "Years with employer must be zero or positive")
    @Max(value = 50, message = "Years with employer cannot exceed 50")
    private Integer yearsWithEmployer;
    
    @Pattern(regexp = "^(SINGLE|MARRIED|DIVORCED|WIDOWED|PARTNERED)$", 
             message = "Marital status must be one of: SINGLE, MARRIED, DIVORCED, WIDOWED, PARTNERED")
    private String maritalStatus;
    
    @Pattern(regexp = "^(CONSERVATIVE|MODERATE|AGGRESSIVE)$", 
             message = "Risk tolerance must be one of: CONSERVATIVE, MODERATE, AGGRESSIVE")
    private String riskTolerance;
    
    // Constructors
    public UserProfileDTO() {}
    
    public UserProfileDTO(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public BigDecimal getAnnualSalary() { return annualSalary; }
    public void setAnnualSalary(BigDecimal annualSalary) { this.annualSalary = annualSalary; }
    
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    
    public Integer getYearsWithEmployer() { return yearsWithEmployer; }
    public void setYearsWithEmployer(Integer yearsWithEmployer) { this.yearsWithEmployer = yearsWithEmployer; }
    
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    
    public String getRiskTolerance() { return riskTolerance; }
    public void setRiskTolerance(String riskTolerance) { this.riskTolerance = riskTolerance; }
    
    /**
     * Get the user's full display name
     */
    public String getDisplayName() {
        return firstName + " " + lastName;
    }
}
