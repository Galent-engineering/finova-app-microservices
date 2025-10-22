package com.finova.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subscriptions")
public class Subscription {
    
    @Id
    private String id;
    
    private Long userId;
    
    private SubscriptionTier tier;  // BASIC, PLUS, PREMIUM
    
    private BillingFrequency frequency;  // MONTHLY, QUARTERLY, ANNUAL
    
    private Double amount;  // Amount per billing cycle
    
    private SubscriptionStatus status;  // ACTIVE, PAUSED, CANCELLED
    
    private PaymentMethod paymentMethod;
    
    private LocalDateTime nextPaymentDate;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Enum definitions
    public enum SubscriptionTier {
        BASIC(50.0),
        PLUS(150.0),
        PREMIUM(500.0);
        
        private final Double basePrice;
        
        SubscriptionTier(Double basePrice) {
            this.basePrice = basePrice;
        }
        
        public Double getBasePrice() {
            return basePrice;
        }
    }
    
    public enum BillingFrequency {
        MONTHLY(1, 1.0),
        QUARTERLY(3, 0.95),  // 5% discount
        ANNUAL(12, 0.85);    // 15% discount
        
        private final int months;
        private final double multiplier;
        
        BillingFrequency(int months, double multiplier) {
            this.months = months;
            this.multiplier = multiplier;
        }
        
        public int getMonths() {
            return months;
        }
        
        public double getMultiplier() {
            return multiplier;
        }
    }
    
    public enum SubscriptionStatus {
        ACTIVE,
        PAUSED,
        CANCELLED
    }
}

