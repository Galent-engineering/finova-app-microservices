package com.finova.payment.service;

import com.finova.payment.dto.PaymentRequestDTO;
import com.finova.payment.dto.PaymentResponseDTO;
import com.finova.payment.model.Payment;
import com.finova.payment.model.Subscription;
import com.finova.payment.repository.PaymentRepository;
import com.finova.payment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {
    
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final Random random = new Random();
    
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing payment for subscription: {}", request.getSubscriptionId());
        
        // Create payment record
        Payment payment = Payment.builder()
                .subscriptionId(request.getSubscriptionId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(Payment.PaymentStatus.PROCESSING)
                .paymentMethodLast4(request.getPaymentMethod().getLast4())
                .retryCount(0)
                .build();
        
        payment = paymentRepository.save(payment);
        
        try {
            // Simulate payment gateway call (mock)
            PaymentGatewayResponse gatewayResponse = mockPaymentGateway(request);
            
            if (gatewayResponse.isSuccess()) {
                // Payment successful
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setTransactionId(gatewayResponse.getTransactionId());
                payment.setProcessedAt(LocalDateTime.now());
                
                // Update subscription next payment date
                updateSubscriptionNextPaymentDate(request.getSubscriptionId());
                
                log.info("Payment completed successfully: {}", payment.getId());
                
                payment = paymentRepository.save(payment);
                return PaymentResponseDTO.success(payment);
                
            } else {
                // Payment failed
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setErrorMessage(gatewayResponse.getErrorMessage());
                payment.setProcessedAt(LocalDateTime.now());
                
                log.warn("Payment failed: {}", gatewayResponse.getErrorMessage());
                
                payment = paymentRepository.save(payment);
                return PaymentResponseDTO.failure(payment, gatewayResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Error processing payment", e);
            
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setErrorMessage("System error: " + e.getMessage());
            payment.setProcessedAt(LocalDateTime.now());
            
            payment = paymentRepository.save(payment);
            return PaymentResponseDTO.failure(payment, "System error occurred");
        }
    }
    
    public List<PaymentResponseDTO> getPaymentHistory(Long userId) {
        log.info("Fetching payment history for user: {}", userId);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentResponseDTO retryPayment(String paymentId) {
        log.info("Retrying payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != Payment.PaymentStatus.FAILED) {
            throw new RuntimeException("Can only retry failed payments");
        }
        
        payment.setRetryCount(payment.getRetryCount() + 1);
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);
        
        // Create request from existing payment
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .subscriptionId(payment.getSubscriptionId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .build();
        
        return processPayment(request);
    }
    
    // Mock Payment Gateway - 90% success rate
    private PaymentGatewayResponse mockPaymentGateway(PaymentRequestDTO request) {
        // Simulate processing delay
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 500-1500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 90% success rate
        boolean success = random.nextDouble() < 0.9;
        
        if (success) {
            return PaymentGatewayResponse.builder()
                    .success(true)
                    .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .build();
        } else {
            String[] errorMessages = {
                "Insufficient funds",
                "Card declined",
                "Expired card",
                "Invalid card number",
                "Payment gateway timeout"
            };
            return PaymentGatewayResponse.builder()
                    .success(false)
                    .errorMessage(errorMessages[random.nextInt(errorMessages.length)])
                    .build();
        }
    }
    
    private void updateSubscriptionNextPaymentDate(String subscriptionId) {
        subscriptionRepository.findById(subscriptionId).ifPresent(subscription -> {
            LocalDateTime nextDate = subscription.getNextPaymentDate()
                    .plusMonths(subscription.getFrequency().getMonths());
            subscription.setNextPaymentDate(nextDate);
            subscriptionRepository.save(subscription);
        });
    }
    
    private PaymentResponseDTO convertToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .subscriptionId(payment.getSubscriptionId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .message(payment.getStatus() == Payment.PaymentStatus.COMPLETED ? 
                        "Payment completed successfully" : payment.getErrorMessage())
                .processedAt(payment.getProcessedAt())
                .success(payment.getStatus() == Payment.PaymentStatus.COMPLETED)
                .build();
    }
    
    // Inner class for mock gateway response
    @lombok.Data
    @lombok.Builder
    private static class PaymentGatewayResponse {
        private boolean success;
        private String transactionId;
        private String errorMessage;
    }
}

