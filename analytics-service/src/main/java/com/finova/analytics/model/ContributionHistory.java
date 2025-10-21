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
@Document(collection = "contribution_history")
@CompoundIndexes({
    @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'contributionDate': -1}")
})
public class ContributionHistory {
    
    @Id
    private String id;
    
    private Long userId;
    
    private LocalDate contributionDate;
    
    private ContributionType type;
    
    private Double amount;
    
    private String accountType;  // Which account received the contribution
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum ContributionType {
        EMPLOYEE_PRETAX,
        EMPLOYEE_ROTH,
        EMPLOYER_MATCH,
        PREVIOUS_BALANCE_TRANSFER,
        CATCHUP
    }
}

