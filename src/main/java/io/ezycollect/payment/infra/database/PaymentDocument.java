package io.ezycollect.payment.infra.database;

import io.ezycollect.payment.domain.model.Payment;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "payments")
public class PaymentDocument {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String cardNumberEncrypted;
    private String maskedCardNumber;
    private Instant createdAt;
    private Instant updatedAt;

    public static PaymentDocument fromDomain(Payment payment) {
        return PaymentDocument.builder()
                .id(payment.getId())
                .firstName(payment.getFirstName())
                .lastName(payment.getLastName())
                .zipCode(payment.getZipCode())
                .cardNumberEncrypted(payment.getCardNumberEncrypted())
                .maskedCardNumber(payment.getMaskedCardNumber())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public Payment toDomain() {
        return Payment.builder()
                .id(this.id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .zipCode(this.zipCode)
                .cardNumberEncrypted(this.cardNumberEncrypted)
                .maskedCardNumber(this.maskedCardNumber)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}