package io.ezycollect.webhook.infra.database.adapter;

import io.ezycollect.webhook.domain.model.Webhook;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "webhooks")
public class WebhookDocument {

    @Id
    private String id;
    private String targetUrl;
    private String eventType;

    public static WebhookDocument fromDomain(Webhook webhook) {
        WebhookDocument document = new WebhookDocument();
        document.setId(webhook.getId());
        document.setTargetUrl(webhook.getTargetUrl());
        return document;
    }

    public static Webhook toDomain(WebhookDocument document) {
        return Webhook.builder()
                .id(document.getId())
                .targetUrl(document.getTargetUrl())
                .build();
    }
}