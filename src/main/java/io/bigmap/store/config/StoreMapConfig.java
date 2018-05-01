package io.bigmap.store.config;

import io.bigmap.store.StoreMap;
import io.bigmap.store.infrastructure.FileMap;
import io.bigmap.store.infrastructure.Index;
import io.bigmap.store.infrastructure.PartitionsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreMapConfig {

    private static final String PARTITIONS_DIR_PATH = "/tmp/bigmap";
    private static final int PARTITION_SIZE_THRESHOLD_BYTES = 1024 * 1024 * 10;

    @Bean
    Index index(PartitionsManager partitionsManager) {
       return new Index(partitionsManager);
    }

    @Bean
    StoreMap storeMap(Index index) {
        return new FileMap(index);
    }

    @Bean
    PartitionsManager partitionsManager() {
        return new PartitionsManager(PARTITIONS_DIR_PATH, PARTITION_SIZE_THRESHOLD_BYTES);
    }
}
