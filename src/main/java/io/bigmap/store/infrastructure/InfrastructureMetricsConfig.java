package io.bigmap.store.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InfrastructureMetricsConfig {

    @Bean
    InfrastructureMetrics infrastructureMetrics() {
        return new InfrastructureMetrics();
    }
}
