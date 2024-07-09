package com.file.transfer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
