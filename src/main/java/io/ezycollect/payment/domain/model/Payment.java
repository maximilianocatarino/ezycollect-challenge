package io.ezycollect.payment.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Payment {

    private String id;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String cardNumberEncrypted;
    private String maskedCardNumber;
    private Instant createdAt;
    private Instant updatedAt;

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            throw new IllegalArgumentException("Card number must be at least 16 digits long");
        }
        String lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        return "************" + lastFourDigits;
    }
}