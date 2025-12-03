package io.ezycollect.webhook.application.port.query;

import io.ezycollect.webhook.domain.model.Webhook;

import java.util.List;

public interface WebhookCommandQuery {
    List<Webhook> findAll();
}
