package io.bigmap.store.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationMetricsConfig {

    @Bean
    ApplicationMetrics applicationMetrics() {
        return new ApplicationMetrics();
    }
}
