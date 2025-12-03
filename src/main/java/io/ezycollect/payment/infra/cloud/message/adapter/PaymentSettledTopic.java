package io.ezycollect.payment.infra.cloud.message.adapter;

import io.ezycollect.shared.infra.cloud.message.adapter.SnsTopic;
import org.springframework.beans.factory.annotation.Value;

public class PaymentSettledTopic extends SnsTopic {
    public PaymentSettledTopic(@Value("${sns.payment-settled-topic}") String topicArn, String message) {
        super(topicArn, message);
    }
}
