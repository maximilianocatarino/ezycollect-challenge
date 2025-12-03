package io.ezycollect.webhook.application.port.command;


import io.ezycollect.webhook.application.port.command.dto.WebhookSubscriptionDTO;
import io.ezycollect.webhook.domain.model.PaymentMessage;
import io.ezycollect.webhook.domain.model.Webhook;

public interface WebhookCommandPort {
    Webhook newSubscriber(WebhookSubscriptionDTO dto);

    boolean fireWebhook(PaymentMessage payment);
}
