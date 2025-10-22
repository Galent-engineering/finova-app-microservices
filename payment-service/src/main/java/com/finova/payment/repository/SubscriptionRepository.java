package com.finova.payment.repository;

import com.finova.payment.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    
    List<Subscription> findByUserId(Long userId);
    
    Optional<Subscription> findByUserIdAndStatus(Long userId, Subscription.SubscriptionStatus status);
    
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
}

