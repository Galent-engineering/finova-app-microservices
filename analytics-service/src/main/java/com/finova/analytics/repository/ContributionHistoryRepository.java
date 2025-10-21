package com.finova.analytics.repository;

import com.finova.analytics.model.ContributionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContributionHistoryRepository extends MongoRepository<ContributionHistory, String> {
    
    List<ContributionHistory> findByUserIdAndContributionDateBetweenOrderByContributionDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    List<ContributionHistory> findByUserIdAndTypeAndContributionDateBetween(
            Long userId, ContributionHistory.ContributionType type, LocalDate startDate, LocalDate endDate);
}

