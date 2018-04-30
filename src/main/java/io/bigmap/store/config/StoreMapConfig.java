package io.bigmap.store.config;

import io.bigmap.store.StoreMap;
import io.bigmap.store.infrastructure.FileMap;
import io.bigmap.store.infrastructure.Index;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreMapConfig {
    @Bean
    Index index() {
       return new Index(FileMap.STANDARD_PATH);
    }

    @Bean
    StoreMap storeMap(Index index) {
        return new FileMap(index, FileMap.STANDARD_PATH);
    }
}
