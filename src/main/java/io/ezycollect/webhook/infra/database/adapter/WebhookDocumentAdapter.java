package io.ezycollect.webhook.infra.database.adapter;

import io.ezycollect.webhook.application.provider.WebhookProvider;
import io.ezycollect.webhook.domain.model.Webhook;
import io.ezycollect.webhook.infra.database.adapter.repository.WebhookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebhookDocumentAdapter implements WebhookProvider {
    private final WebhookRepository repository;

    public WebhookDocumentAdapter(WebhookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Webhook save(Webhook webhook) {
        WebhookDocument document = WebhookDocument.fromDomain(webhook);
        repository.save(document);
        return WebhookDocument.toDomain(document);
    }

    @Override
    public List<Webhook> findByTargetUrl(String targetUrl) {
        return toDomainList(repository.findByTargetUrl(targetUrl));
    }

    @Override
    public List<Webhook> findAll() {
        return toDomainList(repository.findAll());
    }

    private List<Webhook> toDomainList(List<WebhookDocument> documentList) {
        return documentList.stream()
                .map(WebhookDocument::toDomain)
                .collect(Collectors.toList());
    }
}
