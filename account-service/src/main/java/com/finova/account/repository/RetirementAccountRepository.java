package com.finova.account.repository;

import com.finova.account.model.RetirementAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RetirementAccount entity
 */
@Repository
public interface RetirementAccountRepository extends JpaRepository<RetirementAccount, Long> {
    
    /**
     * Find all retirement accounts for a specific user
     */
    List<RetirementAccount> findByUserId(Long userId);
    
    /**
     * Find retirement account by user ID and account type
     */
    Optional<RetirementAccount> findByUserIdAndAccountType(Long userId, String accountType);
    
    /**
     * Find primary retirement account for a user (first account)
     */
    @Query("SELECT ra FROM RetirementAccount ra WHERE ra.userId = :userId ORDER BY ra.createdAt ASC")
    Optional<RetirementAccount> findPrimaryByUserId(@Param("userId") Long userId);
    
    /**
     * Count total accounts for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Find accounts by status
     */
    List<RetirementAccount> findByUserIdAndStatus(Long userId, String status);
}
