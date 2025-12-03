package io.ezycollect.webhook.infra.database.adapter.repository;

import io.ezycollect.webhook.infra.database.adapter.WebhookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookRepository extends MongoRepository<WebhookDocument, String> {
    List<WebhookDocument> findByTargetUrl(String targetUrl);
}
