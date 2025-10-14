package com.finova.account.repository;

import com.finova.account.model.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for IncomeSource entity
 */
@Repository
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long> {
    
    /**
     * Find all income sources for a specific account
     */
    List<IncomeSource> findByAccountId(Long accountId);
    
    /**
     * Find all active income sources for a user
     */
    @Query("SELECT i FROM IncomeSource i WHERE i.account.userId = :userId AND i.active = true")
    List<IncomeSource> findActiveByUserId(@Param("userId") Long userId);
    
    /**
     * Find income sources by type for a user
     */
    @Query("SELECT i FROM IncomeSource i WHERE i.account.userId = :userId AND i.sourceType = :sourceType AND i.active = true")
    List<IncomeSource> findByUserIdAndSourceType(@Param("userId") Long userId, @Param("sourceType") String sourceType);
    
    /**
     * Calculate total projected monthly income for a user
     */
    @Query("SELECT SUM(i.projectedMonthlyIncome) FROM IncomeSource i WHERE i.account.userId = :userId AND i.active = true")
    BigDecimal getTotalProjectedMonthlyIncomeByUserId(@Param("userId") Long userId);
    
    /**
     * Calculate total current balance for a user
     */
    @Query("SELECT SUM(i.currentBalance) FROM IncomeSource i WHERE i.account.userId = :userId AND i.active = true")
    BigDecimal getTotalCurrentBalanceByUserId(@Param("userId") Long userId);
    
    /**
     * Find income sources by provider
     */
    List<IncomeSource> findByProviderAndActive(String provider, boolean active);
}
