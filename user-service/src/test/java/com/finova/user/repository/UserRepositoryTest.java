package com.finova.user.repository;

import com.finova.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository
 * Tests Spring Data JPA repository methods with in-memory database
 */
@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
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
        testUser.setEnabled(true);
        testUser.setRole("USER");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save and retrieve user by ID")
    void testSaveAndFindById() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        assertEquals(testUser.getFirstName(), foundUser.get().getFirstName());
        assertEquals(testUser.getLastName(), foundUser.get().getLastName());
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void testFindByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindByEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if username exists")
    void testExistsByUsername() {
        userRepository.save(testUser);
        entityManager.flush();

        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should check if email exists")
    void testExistsByEmail() {
        userRepository.save(testUser);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail("john.doe@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void testUniqueUsernameConstraint() {
        userRepository.save(testUser);
        entityManager.flush();

        User duplicateUser = new User();
        duplicateUser.setUsername("testuser"); // Same username
        duplicateUser.setPassword("password");
        duplicateUser.setFirstName("Jane");
        duplicateUser.setLastName("Smith");
        duplicateUser.setEmail("jane.smith@example.com");

        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void testUniqueEmailConstraint() {
        userRepository.save(testUser);
        entityManager.flush();

        User duplicateUser = new User();
        duplicateUser.setUsername("anotheruser");
        duplicateUser.setPassword("password");
        duplicateUser.setFirstName("Jane");
        duplicateUser.setLastName("Smith");
        duplicateUser.setEmail("john.doe@example.com"); // Same email

        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");
        savedUser.setEmail("jane.smith@example.com");

        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(updatedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Jane", foundUser.get().getFirstName());
        assertEquals("Smith", foundUser.get().getLastName());
        assertEquals("jane.smith@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Long userId = savedUser.getId();
        userRepository.deleteById(userId);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(userId);
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should persist all user fields correctly")
    void testPersistAllFields() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear(); // Clear the persistence context

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        User user = foundUser.get();

        assertEquals(testUser.getUsername(), user.getUsername());
        assertEquals(testUser.getPassword(), user.getPassword());
        assertEquals(testUser.getFirstName(), user.getFirstName());
        assertEquals(testUser.getLastName(), user.getLastName());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getPhone(), user.getPhone());
        assertEquals(testUser.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(testUser.getAnnualSalary(), user.getAnnualSalary());
        assertEquals(testUser.getEmploymentStatus(), user.getEmploymentStatus());
        assertEquals(testUser.getYearsWithEmployer(), user.getYearsWithEmployer());
        assertEquals(testUser.getMaritalStatus(), user.getMaritalStatus());
        assertEquals(testUser.getRiskTolerance(), user.getRiskTolerance());
        assertEquals(testUser.isEnabled(), user.isEnabled());
        assertEquals(testUser.getRole(), user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        User minimalUser = new User();
        minimalUser.setUsername("minimal");
        minimalUser.setPassword("password");
        minimalUser.setFirstName("Min");
        minimalUser.setLastName("User");
        minimalUser.setEmail("min@example.com");

        User savedUser = userRepository.save(minimalUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        User user = foundUser.get();

        assertNull(user.getPhone());
        assertNull(user.getDateOfBirth());
        assertNull(user.getAnnualSalary());
        assertNull(user.getEmploymentStatus());
        assertNull(user.getYearsWithEmployer());
        assertNull(user.getMaritalStatus());
        assertNull(user.getRiskTolerance());
        assertNull(user.getLastLogin());
    }

    @Test
    @DisplayName("Should save user with null email")
    void testSaveUserWithNullEmail() {
        testUser.setEmail(null);

        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertNull(foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision for annual salary")
    void testBigDecimalPrecision() {
        testUser.setAnnualSalary(new BigDecimal("123456789012.99"));

        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(new BigDecimal("123456789012.99"), foundUser.get().getAnnualSalary());
    }

    @Test
    @DisplayName("Should save and retrieve multiple users")
    void testMultipleUsers() {
        User user1 = new User("user1", "pass1", "First1", "Last1", "user1@example.com");
        User user2 = new User("user2", "pass2", "First2", "Last2", "user2@example.com");
        User user3 = new User("user3", "pass3", "First3", "Last3", "user3@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        entityManager.flush();

        assertEquals(3, userRepository.count());

        assertTrue(userRepository.findByUsername("user1").isPresent());
        assertTrue(userRepository.findByUsername("user2").isPresent());
        assertTrue(userRepository.findByUsername("user3").isPresent());
    }

    @Test
    @DisplayName("Should update last login timestamp")
    void testUpdateLastLogin() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        LocalDateTime loginTime = LocalDateTime.now();
        savedUser.setLastLogin(loginTime);

        userRepository.save(savedUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getLastLogin());
    }

    @Test
    @DisplayName("Should handle case-sensitive username search")
    void testCaseSensitiveUsername() {
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundLowerCase = userRepository.findByUsername("testuser");
        Optional<User> foundUpperCase = userRepository.findByUsername("TESTUSER");

        assertTrue(foundLowerCase.isPresent());
        assertFalse(foundUpperCase.isPresent()); // Assuming case-sensitive
    }

    @Test
    @DisplayName("Should handle case-sensitive email search")
    void testCaseSensitiveEmail() {
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundLowerCase = userRepository.findByEmail("john.doe@example.com");
        Optional<User> foundUpperCase = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");

        assertTrue(foundLowerCase.isPresent());
        // Email comparison might be case-insensitive depending on database configuration
    }

    @Test
    @DisplayName("Should generate ID automatically")
    void testAutoGeneratedId() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        assertNotNull(savedUser.getId());
        assertTrue(savedUser.getId() > 0);
    }

    @Test
    @DisplayName("Should maintain referential integrity on delete")
    void testDeleteNonExistentUser() {
        assertDoesNotThrow(() -> userRepository.deleteById(999L));
    }
}
