package io.ezycollect.webhook.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.ezycollect.webhook.application.port.command.WebhookQueueCommandPort;
import io.ezycollect.webhook.application.port.query.WebhookCommandQuery;
import io.ezycollect.webhook.domain.model.PaymentMessage;
import io.ezycollect.webhook.domain.model.Webhook;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class WebhookQueueService implements WebhookQueueCommandPort {
    private static final Logger log = LoggerFactory.getLogger(WebhookQueueService.class);

    private final ObjectMapper objectMapper;
    private final WebhookCommandQuery webhookCommandQuery;
    private final SqsTemplate sqsTemplate;
    private final String webhookPendingUrl;

    @Override
    public void queueNotifications(PaymentMessage payment) {
        List<Webhook> subscribers = webhookCommandQuery.findAll();
        if (!subscribers.isEmpty()) {
            for (Webhook webhook : subscribers) {
                if (!webhook.getTargetUrl().isEmpty()) {
                    payment.setWebhookURL(webhook.getTargetUrl());
                    try {
                        String payload = objectMapper.writeValueAsString(payment);
                        sqsTemplate.send(webhookPendingUrl, payload);
                    } catch (Exception e) {
                        log.error("Failed to serialize/send webhook payload for url={}", webhook.getTargetUrl(), e);
                    }
                } else {
                    log.error("Invalid webhook URL for id={}", webhook.getId());
                }
            }
        }
    }
}
