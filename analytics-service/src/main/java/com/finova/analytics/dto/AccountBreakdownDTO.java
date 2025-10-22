package com.finova.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBreakdownDTO {
    
    private String accountType;
    private String accountName;
    private Double balance;
    private Double percentage;
    private String color;  // For chart display
}

