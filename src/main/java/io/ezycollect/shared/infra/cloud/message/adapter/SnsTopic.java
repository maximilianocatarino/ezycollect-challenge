package io.ezycollect.shared.infra.cloud.message.adapter;


import lombok.Getter;
import lombok.Setter;


public class SnsTopic {
    @Getter
    protected final String topicArn;

    @Getter
    @Setter
    protected String message;

    public SnsTopic(String topicArn) {
        this.topicArn = topicArn;
    }

    public SnsTopic(String topicArn, String message) {
        this.topicArn = topicArn;
        this.message = message;
    }
}
