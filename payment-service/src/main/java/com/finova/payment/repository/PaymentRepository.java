package com.finova.payment.repository;

import com.finova.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findBySubscriptionId(String subscriptionId);
    
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
}

