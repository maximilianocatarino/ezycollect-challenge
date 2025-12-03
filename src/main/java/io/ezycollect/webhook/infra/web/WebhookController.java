package io.ezycollect.webhook.infra.web;


import io.ezycollect.webhook.application.port.command.WebhookCommandPort;
import io.ezycollect.webhook.application.port.command.dto.WebhookSubscriptionDTO;
import io.ezycollect.webhook.domain.model.Webhook;
import io.ezycollect.webhook.domain.service.WebhookQueueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class WebhookController {
    private final WebhookCommandPort commandPort;

    private static final Logger log = LoggerFactory.getLogger(WebhookQueueService.class);

    @PostMapping("/webhooks/payment")
    public ResponseEntity<Webhook> newSubscriber(@RequestBody WebhookSubscriptionDTO subscription) {
        Webhook webhook = commandPort.newSubscriber(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(webhook);
    }

    @PostMapping("/webhooks/check")
    public ResponseEntity<String> checkWebhook(@RequestParam(name = "id", required = false, defaultValue = "null") String id) {
        log.info("Webhook ID: {}", id);
        return ResponseEntity.ok(id);
    }
}
