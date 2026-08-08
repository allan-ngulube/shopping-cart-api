package com.allan.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class SesConfig {

    @Bean
    public SesV2Client sesClient() {
        return SesV2Client.builder()
                .region(Region.US_EAST_2)
                .build();
    }
}