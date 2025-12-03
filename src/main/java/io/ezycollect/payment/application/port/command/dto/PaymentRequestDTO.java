package io.ezycollect.payment.application.port.command.dto;

import io.ezycollect.payment.domain.model.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class PaymentRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{4,5}(-\\d{3,4})?$", message = "Invalid zip code format, must be 4-5 digits optionally followed by a dash and 3-4 digits")
    private String zipCode;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 20, message = "Card number must be 16 digits without formatting or 20 digits with dashes")
    @Pattern(regexp = "^((\\d{16})|(\\d{4}-\\d{4}-\\d{4}-\\d{4}))$", message = "Card number must only contain digits or dashes")
    private String cardNumber;

    public Payment toDomainModel(String encryptedCardNumber) {
        return Payment.builder()
                .firstName(this.firstName)
                .lastName(this.lastName)
                .zipCode(this.zipCode)
                .maskedCardNumber(this.cardNumber)
                .cardNumberEncrypted(encryptedCardNumber)
                .createdAt(Instant.now())
                .build();
    }
}