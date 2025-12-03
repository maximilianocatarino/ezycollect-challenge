package io.ezycollect.webhook.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.ezycollect.webhook.application.port.command.WebhookCommandPort;
import io.ezycollect.webhook.application.port.command.WebhookQueueCommandPort;
import io.ezycollect.webhook.application.port.query.WebhookCommandQuery;
import io.ezycollect.webhook.application.provider.WebhookProvider;
import io.ezycollect.webhook.domain.service.WebhookQueryService;
import io.ezycollect.webhook.domain.service.WebhookQueueService;
import io.ezycollect.webhook.domain.service.WebhookService;
import io.ezycollect.webhook.infra.database.adapter.WebhookDocumentAdapter;
import io.ezycollect.webhook.infra.database.adapter.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;


@EnableAsync
@Configuration
public class WebhookConfiguration {
    private final String webhookPendingUrl;

    public WebhookConfiguration(@Value("${sqs.pending-webhook-url}") String webhookPendingUrl) {
        this.webhookPendingUrl = webhookPendingUrl;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }

    @Bean
    @Primary
    public WebhookProvider webhookProvider(WebhookRepository repository) {
        return new WebhookDocumentAdapter(repository);
    }

    @Bean
    public WebhookCommandPort webhookCommandPort(RestClient restClient,
                                                 ObjectMapper objectMapper,
                                                 WebhookProvider provider) {
        return new WebhookService(restClient, objectMapper, provider);
    }

    @Bean
    public WebhookCommandQuery webhookCommandQuery(WebhookProvider provider) {
        return new WebhookQueryService(provider);
    }

    @Bean
    public WebhookQueueCommandPort webhookQueueCommandPort(ObjectMapper objectMapper,
                                                           WebhookCommandQuery webhookCommandQuery,
                                                           SqsTemplate sqsTemplate) {
        return new WebhookQueueService(objectMapper,
                                       webhookCommandQuery,
                                       sqsTemplate,
                                       webhookPendingUrl);
    }
}
