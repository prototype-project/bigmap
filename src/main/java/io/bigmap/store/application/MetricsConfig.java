package io.bigmap.store.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    ApplicationMetrics applicationMetrics() {
        return new ApplicationMetrics();
    }
}
