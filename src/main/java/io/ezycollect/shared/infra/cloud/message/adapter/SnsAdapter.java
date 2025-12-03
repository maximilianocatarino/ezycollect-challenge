package io.ezycollect.shared.infra.cloud.message.adapter;

import io.ezycollect.payment.application.provider.MessagePublisherProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class SnsAdapter implements MessagePublisherProvider {
    private static final Logger log = LoggerFactory.getLogger(SnsAdapter.class);
    private final SnsClient snsClient;

    public SnsAdapter(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publishMessage(SnsTopic topic) {
        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(topic.getTopicArn())
                    .message(topic.getMessage())
                    .build();

            snsClient.publish(publishRequest);
        } catch (Exception e) {
            log.error("CRITICAL: Failed to publish to SNS topic {}", topic.getTopicArn(), e);
        }
    }
}
