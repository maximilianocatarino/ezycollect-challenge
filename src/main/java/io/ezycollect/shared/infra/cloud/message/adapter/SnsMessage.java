package io.ezycollect.shared.infra.cloud.message.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsMessage {
    @JsonProperty("Type")
    private String type;

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("TopicArn")
    private String topicArn;

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("Signature")
    private String signature;

    @JsonProperty("SigningCertURL")
    private String signingCertURL;

    @JsonProperty("SignatureVersion")
    private String signatureVersion;

    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;

    @JsonProperty("SubscribeURL")
    private String subscribeURL;

    @JsonProperty("Token")
    private String token;
}