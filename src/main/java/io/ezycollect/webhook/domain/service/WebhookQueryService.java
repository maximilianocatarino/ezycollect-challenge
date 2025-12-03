package io.ezycollect.webhook.domain.service;

import io.ezycollect.webhook.application.port.query.WebhookCommandQuery;
import io.ezycollect.webhook.application.provider.WebhookProvider;
import io.ezycollect.webhook.domain.model.Webhook;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class WebhookQueryService implements WebhookCommandQuery {
    private final WebhookProvider provider;

    @Override
    public List<Webhook> findAll() {
        return provider.findAll();
    }
}
