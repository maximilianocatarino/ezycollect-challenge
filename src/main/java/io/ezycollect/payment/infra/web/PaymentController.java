package io.ezycollect.payment.infra.web;

import io.ezycollect.payment.application.port.command.PaymentRequestCommandPort;
import io.ezycollect.payment.application.port.command.dto.PaymentRequestDTO;
import io.ezycollect.payment.domain.model.Payment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentRequestCommandPort commandPort;

    @PostMapping("/payment")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            Payment payment = commandPort.newPayment(paymentRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            log.error("Failed to create payment", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
