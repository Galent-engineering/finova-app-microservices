package com.finova.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "performance_metrics")
@CompoundIndexes({
    @CompoundIndex(name = "user_period_idx", def = "{'userId': 1, 'periodStart': -1}")
})
public class PerformanceMetric {
    
    @Id
    private String id;
    
    private Long userId;
    
    private LocalDate periodStart;
    
    private LocalDate periodEnd;
    
    private Period period;  // MONTHLY, QUARTERLY, ANNUAL
    
    private Double returnPercentage;
    
    private Double beginningBalance;
    
    private Double endingBalance;
    
    private Double totalContributions;
    
    private Double totalGains;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum Period {
        MONTHLY,
        QUARTERLY,
        ANNUAL
    }
}

