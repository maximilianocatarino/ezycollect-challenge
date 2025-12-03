package io.ezycollect.webhook.application.port.command.dto;

import io.ezycollect.webhook.domain.model.Webhook;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class WebhookSubscriptionDTO {
    @NotBlank(message = "Target URL is required")
    @URL(message = "Target URL must be a valid URL")
    private String targetUrl;

    public Webhook toDomainModel() {
        return Webhook.builder()
                .targetUrl(this.targetUrl)
                .build();
    }
}
