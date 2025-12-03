package io.ezycollect.webhook.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@Jacksonized
public class PaymentMessage {
    private String webhookURL;
    private Payment payment;

    @Getter
    @Setter
    public static class Payment {
        private String id;
        private String firstName;
        private String lastName;
        private String zipCode;
        private String cardNumberEncrypted;
        private String maskedCardNumber;
        private Instant createdAt;
        private Instant updatedAt;
    }
}