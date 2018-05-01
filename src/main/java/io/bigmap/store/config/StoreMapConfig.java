package io.bigmap.store.config;

import io.bigmap.store.infrastructure.FileMap;
import io.bigmap.store.infrastructure.FileMapCleanupScheduler;
import io.bigmap.store.infrastructure.StoreMapFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreMapConfig {

    private static final String PARTITIONS_DIR_PATH = "/tmp/bigmap";
    private static final int PARTITION_SIZE_THRESHOLD_BYTES = 1024 * 1024 * 10;

    @Bean
    FileMap storeMap(StoreMapFactory storeMapFactory) {
        return storeMapFactory.create();
    }

    @Bean
    StoreMapFactory storeMapFactory() {
        return new StoreMapFactory(PARTITIONS_DIR_PATH, PARTITION_SIZE_THRESHOLD_BYTES);
    }

    @Bean
    FileMapCleanupScheduler cleanupScheduler(FileMap fileMap) {
        return new FileMapCleanupScheduler(fileMap);
    }
}