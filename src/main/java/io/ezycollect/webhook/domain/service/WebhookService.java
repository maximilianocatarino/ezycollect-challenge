package io.ezycollect.webhook.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ezycollect.webhook.application.port.command.WebhookCommandPort;
import io.ezycollect.webhook.application.port.command.dto.WebhookSubscriptionDTO;
import io.ezycollect.webhook.application.provider.WebhookProvider;
import io.ezycollect.webhook.domain.model.PaymentMessage;
import io.ezycollect.webhook.domain.model.Webhook;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import java.util.List;


@RequiredArgsConstructor
public class WebhookService implements WebhookCommandPort {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final WebhookProvider provider;

    @Override
    public Webhook newSubscriber(WebhookSubscriptionDTO dto) {
        List<Webhook> list = provider.findByTargetUrl(dto.getTargetUrl());
        if (list.isEmpty()) {
            Webhook webhook = dto.toDomainModel();
            return provider.save(webhook);
        }
        return list.getFirst();
    }

    @Override
    public boolean fireWebhook(PaymentMessage payment) {
        try {
            String payload = objectMapper.writeValueAsString(payment);
            restClient.post()
                    .uri(payment.getWebhookURL())
                    .body(payload)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {

                                log.error("Webhook failed with status {} for URL {}. Response body: {}",
                                        response.getStatusCode(),
                                        payment.getWebhookURL(),
                                        response.getStatusText());
                                throw new RuntimeException("Webhook failed: " + response.getStatusCode());
                            })
                    .toBodilessEntity();

            log.info("Successfully fired webhook to {}", payment.getWebhookURL());
            return true;
        } catch (Exception e) {
            log.error("Failed to fire webhook to {}", payment.getWebhookURL(), e);
        }
        return false;
    }
}
