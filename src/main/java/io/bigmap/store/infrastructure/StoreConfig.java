package io.bigmap.store.infrastructure;

import io.bigmap.store.domain.StoreSetup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
public class StoreConfig {

    private static final String PARTITIONS_DIR_PATH = "/tmp/bigmap";
    private static final int PARTITION_SIZE_THRESHOLD_BYTES = 1024 * 1024 * 10;
    private static final int NUMBER_OF_PUTS_THRESHOLD = 100;

    @Bean
    FileMap storeMap(StoreMapFactory storeMapFactory) {
        return storeMapFactory.create();
    }

    // todo remove factory
    @Bean
    StoreMapFactory storeMapFactory(InfrastructureMetrics infrastructureMetrics) {
        return new StoreMapFactory(
                PARTITIONS_DIR_PATH,
                PARTITION_SIZE_THRESHOLD_BYTES,
                NUMBER_OF_PUTS_THRESHOLD,
                infrastructureMetrics);
    }

    @Bean
    FileMapCleanupScheduler cleanupScheduler(FileMap fileMap) {
        return new FileMapCleanupScheduler(fileMap);
    }

    @Bean
    StoreSetup storeSetup() {
        return new StoreSetup();
    }

    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }

    @Bean
    AsyncHttpReplicaNotifier replicaNotifier(
            AsyncRestTemplate asyncRestTemplate,
            StoreSetup storeSetup,
            InfrastructureMetrics infrastructureMetrics) {
        return new AsyncHttpReplicaNotifier(asyncRestTemplate, storeSetup, infrastructureMetrics);
    }
}