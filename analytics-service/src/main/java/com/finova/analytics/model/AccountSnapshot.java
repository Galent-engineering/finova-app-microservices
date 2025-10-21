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
@Document(collection = "account_snapshots")
@CompoundIndexes({
    @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'snapshotDate': -1}"),
    @CompoundIndex(name = "user_account_date_idx", def = "{'userId': 1, 'accountType': 1, 'snapshotDate': -1}")
})
public class AccountSnapshot {
    
    @Id
    private String id;
    
    private Long userId;
    
    private AccountType accountType;
    
    private LocalDate snapshotDate;
    
    private Double balance;
    
    private Double targetBalance;  // For comparison charts
    
    private Double monthlyContribution;
    
    private Double employeeContribution;
    
    private Double employerMatch;
    
    private Double returnRate;  // Monthly return percentage
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum AccountType {
        TOTAL,           // Aggregate of all accounts
        K401,            // 401(k)
        IRA_TRADITIONAL, // Traditional IRA
        IRA_ROTH,        // Roth IRA
        BROKERAGE,       // Regular brokerage
        PENSION          // Pension
    }
}

