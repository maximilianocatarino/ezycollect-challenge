package io.ezycollect.webhook.application.port.command;

import io.ezycollect.webhook.domain.model.PaymentMessage;

public interface WebhookQueueCommandPort {
    void queueNotifications(PaymentMessage payment);
}
