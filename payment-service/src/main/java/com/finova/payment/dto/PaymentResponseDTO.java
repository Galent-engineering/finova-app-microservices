package com.finova.payment.dto;

import com.finova.payment.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    
    private String paymentId;
    private String subscriptionId;
    private Long userId;
    private Double amount;
    private Payment.PaymentStatus status;
    private String transactionId;
    private String message;
    private LocalDateTime processedAt;
    private Boolean success;
    
    public static PaymentResponseDTO success(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .subscriptionId(payment.getSubscriptionId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .message("Payment processed successfully")
                .processedAt(payment.getProcessedAt())
                .success(true)
                .build();
    }
    
    public static PaymentResponseDTO failure(Payment payment, String errorMessage) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .subscriptionId(payment.getSubscriptionId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .message(errorMessage)
                .processedAt(LocalDateTime.now())
                .success(false)
                .build();
    }
}

