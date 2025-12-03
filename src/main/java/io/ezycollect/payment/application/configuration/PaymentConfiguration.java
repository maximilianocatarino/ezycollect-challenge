package io.ezycollect.payment.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.ezycollect.payment.application.port.command.EncryptionCommandPort;
import io.ezycollect.payment.application.port.command.PaymentRequestCommandPort;
import io.ezycollect.payment.application.provider.MessagePublisherProvider;
import io.ezycollect.payment.application.provider.PaymentProvider;
import io.ezycollect.payment.domain.service.PaymentService;
import io.ezycollect.payment.infra.database.adapter.PaymentDocumentAdapter;
import io.ezycollect.payment.infra.database.adapter.PaymentRepository;
import io.ezycollect.shared.infra.cloud.message.adapter.SnsAdapter;
import io.ezycollect.shared.infra.crypto.AesEncryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsClient;


@Configuration
public class PaymentConfiguration {
    private final String secretKeyBase64;
    private final String topicArn;
    private final String mongoUri;

    public PaymentConfiguration(
            @Value("${encryption.secret-key}") String secretKeyBase64,
            @Value("${sns.payment-settled-topic-arn}") String topicArn,
            @Value("${spring.data.mongodb.uri}") String mongoUri) {
        this.secretKeyBase64 = secretKeyBase64;
        this.topicArn = topicArn;
        this.mongoUri = mongoUri;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public PaymentProvider paymentProvider(PaymentRepository repository) {
        return new PaymentDocumentAdapter(repository);
    }

    @Bean
    public MessagePublisherProvider messagePublisherProvider(SnsClient snsClient) {
        return new SnsAdapter(snsClient);
    }

    @Bean
    public EncryptionCommandPort encryptionCommandPort() {
        return new AesEncryption(secretKeyBase64);
    }

    @Bean
    public PaymentRequestCommandPort paymentRequestCommandPort(PaymentProvider paymentProvider,
                                                               EncryptionCommandPort encryptionCommandPort,
                                                               MessagePublisherProvider messagePublisherProvider,
                                                               ObjectMapper objectMapper) {
        return new PaymentService(paymentProvider,
                                  encryptionCommandPort,
                                  messagePublisherProvider,
                                  objectMapper,
                                  topicArn);
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}
