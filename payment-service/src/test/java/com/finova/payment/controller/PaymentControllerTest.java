package com.finova.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.payment.dto.PaymentRequestDTO;
import com.finova.payment.dto.PaymentResponseDTO;
import com.finova.payment.model.Payment;
import com.finova.payment.model.PaymentMethod;
import com.finova.payment.service.PaymentProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@DisplayName("PaymentController Unit Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentProcessingService paymentProcessingService;

    private PaymentRequestDTO validPaymentRequest;
    private PaymentResponseDTO successPaymentResponse;
    private PaymentResponseDTO failedPaymentResponse;

    @BeforeEach
    void setUp() {
        // Setup valid payment request
        validPaymentRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // Setup success payment response
        successPaymentResponse = PaymentResponseDTO.builder()
                .paymentId("PAY-67890")
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .status(Payment.PaymentStatus.SUCCESS)
                .transactionId("TXN-ABC123")
                .message("Payment processed successfully")
                .processedAt(LocalDateTime.now())
                .success(true)
                .build();

        // Setup failed payment response
        failedPaymentResponse = PaymentResponseDTO.builder()
                .paymentId("PAY-67891")
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .status(Payment.PaymentStatus.FAILED)
                .message("Insufficient funds")
                .processedAt(LocalDateTime.now())
                .success(false)
                .build();
    }

    @Test
    @DisplayName("POST /api/payments/process - Success")
    void testProcessPayment_Success() throws Exception {
        // Given
        when(paymentProcessingService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(successPaymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentId", is("PAY-67890")))
                .andExpect(jsonPath("$.subscriptionId", is("SUB-12345")))
                .andExpect(jsonPath("$.userId", is(1001)))
                .andExpect(jsonPath("$.amount", is(99.99)))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.transactionId", is("TXN-ABC123")))
                .andExpect(jsonPath("$.message", is("Payment processed successfully")))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.processedAt", notNullValue()));

        verify(paymentProcessingService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Failed Payment")
    void testProcessPayment_Failed() throws Exception {
        // Given
        when(paymentProcessingService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(failedPaymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentId", is("PAY-67891")))
                .andExpect(jsonPath("$.status", is("FAILED")))
                .andExpect(jsonPath("$.message", is("Insufficient funds")))
                .andExpect(jsonPath("$.success", is(false)));

        verify(paymentProcessingService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Invalid Request (Missing Subscription ID)")
    void testProcessPayment_MissingSubscriptionId() throws Exception {
        // Given
        PaymentRequestDTO invalidRequest = PaymentRequestDTO.builder()
                .userId(1001L)
                .amount(99.99)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentProcessingService, never()).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Invalid Request (Missing User ID)")
    void testProcessPayment_MissingUserId() throws Exception {
        // Given
        PaymentRequestDTO invalidRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .amount(99.99)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentProcessingService, never()).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Invalid Request (Negative Amount)")
    void testProcessPayment_NegativeAmount() throws Exception {
        // Given
        PaymentRequestDTO invalidRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(-50.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentProcessingService, never()).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Invalid Request (Missing Payment Method)")
    void testProcessPayment_MissingPaymentMethod() throws Exception {
        // Given
        PaymentRequestDTO invalidRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .build();

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentProcessingService, never()).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/payments/history/{userId} - Success with Multiple Payments")
    void testGetPaymentHistory_Success() throws Exception {
        // Given
        Long userId = 1001L;
        List<PaymentResponseDTO> paymentHistory = Arrays.asList(
                PaymentResponseDTO.builder()
                        .paymentId("PAY-001")
                        .subscriptionId("SUB-12345")
                        .userId(userId)
                        .amount(99.99)
                        .status(Payment.PaymentStatus.SUCCESS)
                        .transactionId("TXN-001")
                        .message("Payment processed successfully")
                        .processedAt(LocalDateTime.now().minusDays(30))
                        .success(true)
                        .build(),
                PaymentResponseDTO.builder()
                        .paymentId("PAY-002")
                        .subscriptionId("SUB-12345")
                        .userId(userId)
                        .amount(99.99)
                        .status(Payment.PaymentStatus.SUCCESS)
                        .transactionId("TXN-002")
                        .message("Payment processed successfully")
                        .processedAt(LocalDateTime.now())
                        .success(true)
                        .build()
        );

        when(paymentProcessingService.getPaymentHistory(userId))
                .thenReturn(paymentHistory);

        // When & Then
        mockMvc.perform(get("/api/payments/history/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].paymentId", is("PAY-001")))
                .andExpect(jsonPath("$[0].userId", is(1001)))
                .andExpect(jsonPath("$[0].status", is("SUCCESS")))
                .andExpect(jsonPath("$[1].paymentId", is("PAY-002")))
                .andExpect(jsonPath("$[1].userId", is(1001)))
                .andExpect(jsonPath("$[1].status", is("SUCCESS")));

        verify(paymentProcessingService, times(1)).getPaymentHistory(userId);
    }

    @Test
    @DisplayName("GET /api/payments/history/{userId} - Empty History")
    void testGetPaymentHistory_EmptyHistory() throws Exception {
        // Given
        Long userId = 9999L;
        when(paymentProcessingService.getPaymentHistory(userId))
                .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/payments/history/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(paymentProcessingService, times(1)).getPaymentHistory(userId);
    }

    @Test
    @DisplayName("POST /api/payments/{paymentId}/retry - Success")
    void testRetryPayment_Success() throws Exception {
        // Given
        String paymentId = "PAY-67891";
        PaymentResponseDTO retryResponse = PaymentResponseDTO.builder()
                .paymentId(paymentId)
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .status(Payment.PaymentStatus.SUCCESS)
                .transactionId("TXN-RETRY-001")
                .message("Payment retry successful")
                .processedAt(LocalDateTime.now())
                .success(true)
                .build();

        when(paymentProcessingService.retryPayment(paymentId))
                .thenReturn(retryResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/{paymentId}/retry", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentId", is(paymentId)))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.transactionId", is("TXN-RETRY-001")))
                .andExpect(jsonPath("$.message", is("Payment retry successful")))
                .andExpect(jsonPath("$.success", is(true)));

        verify(paymentProcessingService, times(1)).retryPayment(paymentId);
    }

    @Test
    @DisplayName("POST /api/payments/{paymentId}/retry - Failed Retry")
    void testRetryPayment_Failed() throws Exception {
        // Given
        String paymentId = "PAY-67891";
        PaymentResponseDTO retryResponse = PaymentResponseDTO.builder()
                .paymentId(paymentId)
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .status(Payment.PaymentStatus.FAILED)
                .message("Payment retry failed - Card declined")
                .processedAt(LocalDateTime.now())
                .success(false)
                .build();

        when(paymentProcessingService.retryPayment(paymentId))
                .thenReturn(retryResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/{paymentId}/retry", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentId", is(paymentId)))
                .andExpect(jsonPath("$.status", is("FAILED")))
                .andExpect(jsonPath("$.message", is("Payment retry failed - Card declined")))
                .andExpect(jsonPath("$.success", is(false)));

        verify(paymentProcessingService, times(1)).retryPayment(paymentId);
    }

    @Test
    @DisplayName("POST /api/payments/process - CORS Headers Present")
    void testProcessPayment_CorsEnabled() throws Exception {
        // Given
        when(paymentProcessingService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(successPaymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest))
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk());

        verify(paymentProcessingService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/payments/process - Different Payment Methods")
    void testProcessPayment_DifferentPaymentMethods() throws Exception {
        // Test with DEBIT_CARD
        PaymentRequestDTO debitCardRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .paymentMethod(PaymentMethod.DEBIT_CARD)
                .build();

        when(paymentProcessingService.processPayment(any(PaymentRequestDTO.class)))
                .thenReturn(successPaymentResponse);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debitCardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Test with BANK_TRANSFER
        PaymentRequestDTO bankTransferRequest = PaymentRequestDTO.builder()
                .subscriptionId("SUB-12345")
                .userId(1001L)
                .amount(99.99)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bankTransferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(paymentProcessingService, times(2)).processPayment(any(PaymentRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/payments/history/{userId} - Large User ID")
    void testGetPaymentHistory_LargeUserId() throws Exception {
        // Given
        Long largeUserId = 999999999L;
        when(paymentProcessingService.getPaymentHistory(largeUserId))
                .thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/payments/history/{userId}", largeUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(paymentProcessingService, times(1)).getPaymentHistory(largeUserId);
    }
}
