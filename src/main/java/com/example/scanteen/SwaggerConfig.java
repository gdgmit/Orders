package com.example.scanteen;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;


@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi apiDocket() {
        return GroupedOpenApi.builder()
                .group("api") // Optional: Group name for APIs
                .pathsToMatch("/**") // Define the path patterns for API endpoints
                .packagesToScan("com.example.scanteen") // Replace with your package
                .build();
    }
}