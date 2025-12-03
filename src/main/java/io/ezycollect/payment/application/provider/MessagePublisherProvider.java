package io.ezycollect.payment.application.provider;

import io.ezycollect.shared.infra.cloud.message.adapter.SnsTopic;

public interface MessagePublisherProvider {
    void publishMessage(SnsTopic topic);
}
