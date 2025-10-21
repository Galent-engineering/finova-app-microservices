package com.finova.analytics.repository;

import com.finova.analytics.model.AccountSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountSnapshotRepository extends MongoRepository<AccountSnapshot, String> {
    
    List<AccountSnapshot> findByUserIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    List<AccountSnapshot> findByUserIdAndAccountTypeAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            Long userId, AccountSnapshot.AccountType accountType, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'userId': ?0, 'snapshotDate': ?1 }")
    List<AccountSnapshot> findByUserIdAndDate(Long userId, LocalDate date);
    
    @Query("{ 'userId': ?0, 'accountType': ?1, 'snapshotDate': { $lte: ?2 } }")
    List<AccountSnapshot> findLatestByUserIdAndAccountType(
            Long userId, AccountSnapshot.AccountType accountType, LocalDate beforeDate);
}

