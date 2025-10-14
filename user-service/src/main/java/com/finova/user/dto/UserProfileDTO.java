package com.finova.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * DTO for user profile information
 */
public class UserProfileDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String phone;
    private LocalDate dateOfBirth;
    private BigDecimal annualSalary;
    private String employmentStatus;
    private Integer yearsWithEmployer;
    private String maritalStatus;
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
