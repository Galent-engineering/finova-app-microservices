package com.finova.payment.controller;

import com.finova.payment.dto.PaymentRequestDTO;
import com.finova.payment.dto.PaymentResponseDTO;
import com.finova.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentProcessingService paymentProcessingService;
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        log.info("POST /api/payments/process");
        PaymentResponseDTO response = paymentProcessingService.processPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentHistory(@PathVariable Long userId) {
        log.info("GET /api/payments/history/{}", userId);
        List<PaymentResponseDTO> history = paymentProcessingService.getPaymentHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<PaymentResponseDTO> retryPayment(@PathVariable String paymentId) {
        log.info("POST /api/payments/{}/retry", paymentId);
        PaymentResponseDTO response = paymentProcessingService.retryPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}

