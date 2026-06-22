package de.ait.g_67_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class DOClientConfig {

    @Bean
    public S3Client doClient(DOProperties doProperties) {
        String accessKey = doProperties.getAccessKey();
        String secretKey = doProperties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        //KeyProvider
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // Region object
        String region = doProperties.getRegion();
        Region regionInstance = Region.of(region);

        // Address object
        String endpoint = doProperties.getEndpoint();
        URI endpointUri = URI.create(endpoint);

        // Create client for connection to DO servers
        return S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(regionInstance)
                .endpointOverride(endpointUri)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}
