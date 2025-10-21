package com.finova.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
    
    private PaymentType type;  // CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT
    
    private String last4;  // Last 4 digits of card/account
    
    private String brand;  // Visa, Mastercard, etc.
    
    private String expiryMonth;
    
    private String expiryYear;
    
    private String bankName;  // For bank accounts
    
    private String holderName;
    
    public enum PaymentType {
        CREDIT_CARD,
        DEBIT_CARD,
        BANK_ACCOUNT
    }
    
    public String getMaskedNumber() {
        if (last4 == null) {
            return "****";
        }
        return "****" + last4;
    }
}

