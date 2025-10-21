package com.finova.payment.controller;

import com.finova.payment.dto.SubscriptionDTO;
import com.finova.payment.model.PaymentMethod;
import com.finova.payment.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptions(@PathVariable Long userId) {
        log.info("GET /api/subscriptions/{}", userId);
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }
    
    @GetMapping("/{userId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscription(@PathVariable Long userId) {
        log.info("GET /api/subscriptions/{}/active", userId);
        return subscriptionService.getActiveSubscription(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO dto) {
        log.info("POST /api/subscriptions");
        SubscriptionDTO created = subscriptionService.createSubscription(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
            @PathVariable String id,
            @Valid @RequestBody SubscriptionDTO dto) {
        log.info("PUT /api/subscriptions/{}", id);
        SubscriptionDTO updated = subscriptionService.updateSubscription(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable String id) {
        log.info("DELETE /api/subscriptions/{}", id);
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/pause")
    public ResponseEntity<Void> pauseSubscription(@PathVariable String id) {
        log.info("PATCH /api/subscriptions/{}/pause", id);
        subscriptionService.pauseSubscription(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/payment-method")
    public ResponseEntity<SubscriptionDTO> updatePaymentMethod(
            @PathVariable String id,
            @Valid @RequestBody PaymentMethod paymentMethod) {
        log.info("PUT /api/subscriptions/{}/payment-method", id);
        SubscriptionDTO updated = subscriptionService.updatePaymentMethod(id, paymentMethod);
        return ResponseEntity.ok(updated);
    }
}

