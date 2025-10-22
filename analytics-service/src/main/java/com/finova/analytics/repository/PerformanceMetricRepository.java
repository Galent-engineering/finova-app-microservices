package com.finova.analytics.repository;

import com.finova.analytics.model.PerformanceMetric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerformanceMetricRepository extends MongoRepository<PerformanceMetric, String> {
    
    List<PerformanceMetric> findByUserIdAndPeriodStartBetweenOrderByPeriodStartAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    List<PerformanceMetric> findByUserIdAndPeriodOrderByPeriodStartDesc(
            Long userId, PerformanceMetric.Period period);
}

