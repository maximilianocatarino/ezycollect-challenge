package io.ezycollect.payment.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ezycollect.payment.application.port.command.EncryptionCommandPort;
import io.ezycollect.payment.application.port.command.PaymentRequestCommandPort;
import io.ezycollect.payment.application.port.command.dto.PaymentRequestDTO;
import io.ezycollect.payment.application.provider.MessagePublisherProvider;
import io.ezycollect.payment.application.provider.PaymentProvider;
import io.ezycollect.payment.domain.event.PaymentEvent;
import io.ezycollect.payment.domain.model.Payment;
import io.ezycollect.payment.infra.cloud.message.adapter.PaymentSettledTopic;
import io.ezycollect.shared.infra.cloud.message.adapter.SnsTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class PaymentService implements PaymentRequestCommandPort {
    private final PaymentProvider paymentProvider;
    private final EncryptionCommandPort encryptionCommandPort;
    private final MessagePublisherProvider messagePublisherProvider;
    private final ObjectMapper objectMapper;
    private final String paymentSettledTopicArn;

    @Transactional
    public Payment newPayment(PaymentRequestDTO requestDTO) {
        String encryptedCard = encryptionCommandPort.encrypt(requestDTO.getCardNumber());
        Payment payment = requestDTO.toDomainModel(encryptedCard);
        payment.setMaskedCardNumber(Payment.maskCardNumber(payment.getMaskedCardNumber()));
        Payment saved = paymentProvider.save(payment);

        dispatchEvent(new PaymentEvent(PaymentEvent.EventType.PAYMENT_SETTLED, saved));

        return saved;
    }

    @Async
    private void dispatchEvent(PaymentEvent event) {
        try {
            if (event.getEventType() == PaymentEvent.EventType.PAYMENT_SETTLED) {
                String payload = objectMapper.writeValueAsString(event.getPayment());
                SnsTopic topic = new PaymentSettledTopic(this.paymentSettledTopicArn, payload);
                messagePublisherProvider.publishMessage(topic);
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to dispatch payment event: " + e.getMessage());
        }
    }
}