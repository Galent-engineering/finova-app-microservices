package com.finova.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardDTO {
    
    private KeyStatsDTO keyStats;
    private List<SavingsGrowthDataPoint> savingsGrowth;
    private ContributionBreakdownDTO contributionBreakdown;
    private List<AccountBreakdownDTO> accountBreakdown;
    private QuarterlyReturnsDTO quarterlyReturns;
    private List<InsightDTO> insights;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyStatsDTO {
        private Double totalAssets;
        private String totalAssetsTrend;  // "up" or "down"
        private Double trendPercentage;
        
        private Double annualContribution;
        private String annualContributionTrend;
        private Double contributionTrendPercentage;
        
        private Double ytdReturn;
        private String ytdReturnTrend;
        private Double returnTrendPercentage;
        
        private Integer onTrackScore;  // 0-100
        private String onTrackStatus;  // "On Track", "Needs Attention", "Excellent"
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavingsGrowthDataPoint {
        private String month;
        private Double actualBalance;
        private Double targetBalance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContributionBreakdownDTO {
        private Double employeeContributions;
        private Double employerMatch;
        private Double previousBalance;
        private Double total;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuarterlyReturnsDTO {
        private Double q1Return;
        private Double q2Return;
        private Double q3Return;
        private Double q4Return;
        private Double ytdAverage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsightDTO {
        private String title;
        private String description;
        private String type;  // "positive", "warning", "info"
        private String icon;  // Icon name for frontend
    }
}

