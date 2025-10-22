package com.finova.payment.service;

import com.finova.payment.dto.SubscriptionDTO;
import com.finova.payment.model.PaymentMethod;
import com.finova.payment.model.Subscription;
import com.finova.payment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    
    public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId) {
        log.info("Fetching subscriptions for user: {}", userId);
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<SubscriptionDTO> getActiveSubscription(Long userId) {
        log.info("Fetching active subscription for user: {}", userId);
        return subscriptionRepository.findByUserIdAndStatus(userId, Subscription.SubscriptionStatus.ACTIVE)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionDTO dto) {
        log.info("Creating subscription for user: {}", dto.getUserId());
        
        // Calculate amount based on tier and frequency
        Double basePrice = dto.getTier().getBasePrice();
        Double amount = basePrice * dto.getFrequency().getMonths() * dto.getFrequency().getMultiplier();
        
        Subscription subscription = Subscription.builder()
                .userId(dto.getUserId())
                .tier(dto.getTier())
                .frequency(dto.getFrequency())
                .amount(amount)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .paymentMethod(dto.getPaymentMethod())
                .startDate(LocalDateTime.now())
                .nextPaymentDate(calculateNextPaymentDate(dto.getFrequency()))
                .build();
        
        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription created with ID: {}", saved.getId());
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public SubscriptionDTO updateSubscription(String id, SubscriptionDTO dto) {
        log.info("Updating subscription: {}", id);
        
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        
        // Update fields if provided
        if (dto.getTier() != null) {
            subscription.setTier(dto.getTier());
        }
        if (dto.getFrequency() != null) {
            subscription.setFrequency(dto.getFrequency());
            subscription.setNextPaymentDate(calculateNextPaymentDate(dto.getFrequency()));
        }
        if (dto.getPaymentMethod() != null) {
            subscription.setPaymentMethod(dto.getPaymentMethod());
        }
        if (dto.getStatus() != null) {
            subscription.setStatus(dto.getStatus());
        }
        
        // Recalculate amount
        Double basePrice = subscription.getTier().getBasePrice();
        Double amount = basePrice * subscription.getFrequency().getMonths() 
                        * subscription.getFrequency().getMultiplier();
        subscription.setAmount(amount);
        
        Subscription updated = subscriptionRepository.save(subscription);
        log.info("Subscription updated: {}", id);
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public void cancelSubscription(String id) {
        log.info("Cancelling subscription: {}", id);
        
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        
        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        subscription.setEndDate(LocalDateTime.now());
        
        subscriptionRepository.save(subscription);
        log.info("Subscription cancelled: {}", id);
    }
    
    @Transactional
    public void pauseSubscription(String id) {
        log.info("Pausing subscription: {}", id);
        
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        
        subscription.setStatus(Subscription.SubscriptionStatus.PAUSED);
        
        subscriptionRepository.save(subscription);
        log.info("Subscription paused: {}", id);
    }
    
    @Transactional
    public SubscriptionDTO updatePaymentMethod(String id, PaymentMethod paymentMethod) {
        log.info("Updating payment method for subscription: {}", id);
        
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        
        subscription.setPaymentMethod(paymentMethod);
        
        Subscription updated = subscriptionRepository.save(subscription);
        log.info("Payment method updated for subscription: {}", id);
        
        return convertToDTO(updated);
    }
    
    private LocalDateTime calculateNextPaymentDate(Subscription.BillingFrequency frequency) {
        return LocalDateTime.now().plusMonths(frequency.getMonths());
    }
    
    private SubscriptionDTO convertToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .tier(subscription.getTier())
                .frequency(subscription.getFrequency())
                .amount(subscription.getAmount())
                .status(subscription.getStatus())
                .paymentMethod(subscription.getPaymentMethod())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}

