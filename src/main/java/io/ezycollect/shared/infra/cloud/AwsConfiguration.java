package io.ezycollect.shared.infra.cloud;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;


@Configuration
public class AwsConfiguration {

    private static final Region LOCAL_REGION = Region.of("us-east-1");

    @Bean
    @Profile("local")
    public SqsAsyncClient localStackSqsAsyncClient(
            @Value("${cloud.aws.sqs.endpoint}") String endpoint) {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(endpoint))
                // LocalStack does not require real credentials/region
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(LOCAL_REGION)
                .build();
    }

    @Bean
    @Profile("local")
    public SnsClient localStackSnsClient(
            @Value("${cloud.aws.sqs.endpoint}") String endpoint) {
        return SnsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(LOCAL_REGION)
                .build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }
}
