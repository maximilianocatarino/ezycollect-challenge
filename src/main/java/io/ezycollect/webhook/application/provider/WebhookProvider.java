package io.ezycollect.webhook.application.provider;

import io.ezycollect.webhook.domain.model.Webhook;

import java.util.List;

public interface WebhookProvider {
    Webhook save(Webhook webhook);
    List<Webhook> findAll();
    List<Webhook> findByTargetUrl(String targetUrl);
}
