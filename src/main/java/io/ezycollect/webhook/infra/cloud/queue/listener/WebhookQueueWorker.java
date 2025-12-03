package io.ezycollect.webhook.infra.cloud.queue.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.ezycollect.shared.infra.cloud.message.adapter.SnsMessage;
import io.ezycollect.webhook.application.port.command.WebhookCommandPort;
import io.ezycollect.webhook.application.port.command.WebhookQueueCommandPort;
import io.ezycollect.webhook.domain.model.PaymentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class WebhookQueueWorker {
    private final ObjectMapper objectMapper;
    private final WebhookCommandPort webhookCommandPort;
    private final WebhookQueueCommandPort webhookQueueCommandPort;

    private PaymentMessage createFromPaymentString(String payload) throws JsonProcessingException {
        PaymentMessage.Payment payment = objectMapper.readValue(payload, PaymentMessage.Payment.class);
        return PaymentMessage.builder()
                .payment(payment)
                .build();
    }

    private PaymentMessage createFromSnsStringMessage(String snsMessageJson) throws JsonProcessingException {
        SnsMessage sns = objectMapper.readValue(snsMessageJson, SnsMessage.class);
        if (sns == null || sns.getMessage() == null) {
            throw new IllegalArgumentException("Invalid SNS message received");
        }
        return createFromPaymentString(sns.getMessage());
    }

    @SqsListener(value = "${sqs.queue-webhook-url}")
    public void handleSettledPaymentQueue(String snsMessageJson) {
        String paymentId = null;
        try {
            PaymentMessage message = createFromSnsStringMessage(snsMessageJson);
            paymentId = message.getPayment().getId();
            webhookQueueCommandPort.queueNotifications(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to queue webhooks for payment ID: " + paymentId, e);
        }
    }

    @SqsListener(value = "${sqs.pending-webhook-url}")
    public void handleSettledPaymentNotification(String paymentMessage) {
        String paymentId = null;
        try {
            PaymentMessage message = objectMapper.readValue(paymentMessage, PaymentMessage.class);
            paymentId = message.getPayment().getId();
            webhookCommandPort.fireWebhook(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send webhooks for payment ID: " + paymentId, e);
        }
    }
}
