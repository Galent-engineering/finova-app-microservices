package com.finova.analytics.service;

import com.finova.analytics.model.AccountSnapshot;
import com.finova.analytics.model.ContributionHistory;
import com.finova.analytics.model.PerformanceMetric;
import com.finova.analytics.repository.AccountSnapshotRepository;
import com.finova.analytics.repository.ContributionHistoryRepository;
import com.finova.analytics.repository.PerformanceMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataGenerationService {
    
    private final AccountSnapshotRepository accountSnapshotRepository;
    private final ContributionHistoryRepository contributionHistoryRepository;
    private final PerformanceMetricRepository performanceMetricRepository;
    private final Random random = new Random();
    
    @EventListener(ApplicationReadyEvent.class)
    public void generateSampleDataOnStartup() {
        log.info("Checking if sample data needs to be generated...");
        
        // Only generate if no data exists
        if (accountSnapshotRepository.count() == 0) {
            log.info("No existing data found. Generating 6 months of sample data...");
            generateSixMonthsOfData(1L);  // For user ID 1
            log.info("Sample data generation complete!");
        } else {
            log.info("Existing data found. Skipping sample data generation.");
        }
    }
    
    public void generateSixMonthsOfData(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        
        // Starting balances
        double k401Balance = 95000.0;
        double iraTraditionalBalance = 22000.0;
        double iraRothBalance = 10000.0;
        double brokerageBalance = 5000.0;
        
        // Monthly growth rates (simulating 7-8% annual return)
        double monthlyReturnRate = 0.006; // ~7.2% annually
        
        // Monthly contributions
        double monthlyEmployeeContribution = 650.0;
        double monthlyEmployerMatch = 325.0;
        
        List<AccountSnapshot> snapshots = new ArrayList<>();
        List<ContributionHistory> contributions = new ArrayList<>();
        List<PerformanceMetric> metrics = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            // Add monthly contributions
            contributions.add(ContributionHistory.builder()
                    .userId(userId)
                    .contributionDate(currentDate)
                    .type(ContributionHistory.ContributionType.EMPLOYEE_PRETAX)
                    .amount(monthlyEmployeeContribution)
                    .accountType("401K")
                    .build());
            
            contributions.add(ContributionHistory.builder()
                    .userId(userId)
                    .contributionDate(currentDate)
                    .type(ContributionHistory.ContributionType.EMPLOYER_MATCH)
                    .amount(monthlyEmployerMatch)
                    .accountType("401K")
                    .build());
            
            // Apply growth and contributions
            double returnVariance = (random.nextDouble() - 0.5) * 0.002; // ±0.2%
            double monthlyReturn = monthlyReturnRate + returnVariance;
            
            k401Balance = k401Balance * (1 + monthlyReturn) + monthlyEmployeeContribution + monthlyEmployerMatch;
            iraTraditionalBalance = iraTraditionalBalance * (1 + monthlyReturn);
            iraRothBalance = iraRothBalance * (1 + monthlyReturn);
            brokerageBalance = brokerageBalance * (1 + monthlyReturn);
            
            double totalBalance = k401Balance + iraTraditionalBalance + iraRothBalance + brokerageBalance;
            
            // Create snapshots for each account type
            snapshots.add(createSnapshot(userId, currentDate, AccountSnapshot.AccountType.K401, 
                    k401Balance, k401Balance * 1.05, monthlyEmployeeContribution + monthlyEmployerMatch,
                    monthlyEmployeeContribution, monthlyEmployerMatch, monthlyReturn * 100));
            
            snapshots.add(createSnapshot(userId, currentDate, AccountSnapshot.AccountType.IRA_TRADITIONAL, 
                    iraTraditionalBalance, iraTraditionalBalance * 1.05, 0.0, 0.0, 0.0, monthlyReturn * 100));
            
            snapshots.add(createSnapshot(userId, currentDate, AccountSnapshot.AccountType.IRA_ROTH, 
                    iraRothBalance, iraRothBalance * 1.05, 0.0, 0.0, 0.0, monthlyReturn * 100));
            
            snapshots.add(createSnapshot(userId, currentDate, AccountSnapshot.AccountType.BROKERAGE, 
                    brokerageBalance, brokerageBalance * 1.05, 0.0, 0.0, 0.0, monthlyReturn * 100));
            
            // Total snapshot
            snapshots.add(createSnapshot(userId, currentDate, AccountSnapshot.AccountType.TOTAL, 
                    totalBalance, totalBalance * 1.05, monthlyEmployeeContribution + monthlyEmployerMatch,
                    monthlyEmployeeContribution, monthlyEmployerMatch, monthlyReturn * 100));
            
            currentDate = currentDate.plusMonths(1);
        }
        
        // Generate quarterly performance metrics
        LocalDate quarterStart = startDate;
        while (!quarterStart.isAfter(endDate)) {
            LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
            if (quarterEnd.isAfter(endDate)) {
                quarterEnd = endDate;
            }
            
            double quarterlyReturn = 1.5 + (random.nextDouble() * 2.0); // 1.5% - 3.5%
            
            metrics.add(PerformanceMetric.builder()
                    .userId(userId)
                    .periodStart(quarterStart)
                    .periodEnd(quarterEnd)
                    .period(PerformanceMetric.Period.QUARTERLY)
                    .returnPercentage(quarterlyReturn)
                    .beginningBalance(100000.0)
                    .endingBalance(100000.0 * (1 + quarterlyReturn / 100))
                    .totalContributions(975.0 * 3)
                    .totalGains(100000.0 * (quarterlyReturn / 100))
                    .build());
            
            quarterStart = quarterStart.plusMonths(3);
        }
        
        // Save all data
        log.info("Saving {} account snapshots...", snapshots.size());
        accountSnapshotRepository.saveAll(snapshots);
        
        log.info("Saving {} contribution records...", contributions.size());
        contributionHistoryRepository.saveAll(contributions);
        
        log.info("Saving {} performance metrics...", metrics.size());
        performanceMetricRepository.saveAll(metrics);
        
        log.info("Sample data generation completed successfully!");
    }
    
    private AccountSnapshot createSnapshot(Long userId, LocalDate date, AccountSnapshot.AccountType type,
                                         double balance, double targetBalance, double monthlyContribution,
                                         double employeeContribution, double employerMatch, double returnRate) {
        return AccountSnapshot.builder()
                .userId(userId)
                .accountType(type)
                .snapshotDate(date)
                .balance(balance)
                .targetBalance(targetBalance)
                .monthlyContribution(monthlyContribution)
                .employeeContribution(employeeContribution)
                .employerMatch(employerMatch)
                .returnRate(returnRate)
                .build();
    }
}

