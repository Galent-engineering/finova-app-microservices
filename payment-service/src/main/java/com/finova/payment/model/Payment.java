package com.finova.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    
    @Id
    private String id;
    
    private String subscriptionId;
    
    private Long userId;
    
    private Double amount;
    
    private PaymentStatus status;
    
    private String transactionId;  // Mock gateway transaction ID
    
    private String paymentMethodLast4;
    
    private String errorMessage;
    
    private Integer retryCount;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private LocalDateTime processedAt;
    
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}

