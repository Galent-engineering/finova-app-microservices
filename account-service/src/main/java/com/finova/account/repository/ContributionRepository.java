package com.finova.account.repository;

import com.finova.account.model.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Contribution entity
 */
@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    
    /**
     * Find all contributions for a specific account
     */
    List<Contribution> findByAccountId(Long accountId);
    
    /**
     * Find contribution by account and type
     */
    Optional<Contribution> findByAccountIdAndContributionType(Long accountId, String contributionType);
    
    /**
     * Find all contributions for a user's accounts
     */
    @Query("SELECT c FROM Contribution c WHERE c.account.userId = :userId")
    List<Contribution> findByUserId(@Param("userId") Long userId);
    
    /**
     * Calculate total monthly contributions for an account
     */
    @Query("SELECT SUM(c.monthlyAmount) FROM Contribution c WHERE c.account.id = :accountId")
    BigDecimal getTotalMonthlyContributionsByAccountId(@Param("accountId") Long accountId);
    
    /**
     * Calculate total annual contributions for an account
     */
    @Query("SELECT SUM(c.annualAmount) FROM Contribution c WHERE c.account.id = :accountId")
    BigDecimal getTotalAnnualContributionsByAccountId(@Param("accountId") Long accountId);
    
    /**
     * Find contributions by type for a user
     */
    @Query("SELECT c FROM Contribution c WHERE c.account.userId = :userId AND c.contributionType = :type")
    List<Contribution> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String contributionType);
}
