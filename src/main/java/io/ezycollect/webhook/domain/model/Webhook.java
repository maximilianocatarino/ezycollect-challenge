package io.ezycollect.webhook.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Webhook {
    private String id;
    private String targetUrl;
}
