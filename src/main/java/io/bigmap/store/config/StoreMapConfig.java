package io.bigmap.store.config;

import io.bigmap.store.StoreMap;
import io.bigmap.store.infrastructure.FileMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreMapConfig {

    @Bean
    StoreMap storeMap() {
        return new FileMap();
    }
}
