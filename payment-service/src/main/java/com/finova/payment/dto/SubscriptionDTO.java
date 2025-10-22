package com.finova.payment.dto;

import com.finova.payment.model.PaymentMethod;
import com.finova.payment.model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    
    private String id;
    private Long userId;
    private Subscription.SubscriptionTier tier;
    private Subscription.BillingFrequency frequency;
    private Double amount;
    private Subscription.SubscriptionStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime nextPaymentDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Helper method to get formatted tier description
    public String getTierDescription() {
        if (tier == null) return "";
        return switch (tier) {
            case BASIC -> "Basic Advisory Tier - Essential retirement planning";
            case PLUS -> "Plus Advisory Tier - Enhanced guidance and support";
            case PREMIUM -> "Premium Advisory Tier - Full-service advisory with dedicated support";
        };
    }
    
    // Helper method to get frequency description
    public String getFrequencyDescription() {
        if (frequency == null) return "";
        return switch (frequency) {
            case MONTHLY -> "Monthly billing";
            case QUARTERLY -> "Quarterly billing (5% discount)";
            case ANNUAL -> "Annual billing (15% discount)";
        };
    }
}

