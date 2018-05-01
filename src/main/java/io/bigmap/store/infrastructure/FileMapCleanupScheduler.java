package io.bigmap.store.infrastructure;

import org.springframework.scheduling.annotation.Scheduled;

public class FileMapCleanupScheduler {
    private static final long REFRESH_RATE_IN_SECONDS = 10;
    private final FileMap storeMap;

    public FileMapCleanupScheduler(FileMap storeMap) {
        this.storeMap = storeMap;
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
            initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    public void cleanupStore() {
        storeMap.cleanup();
    }
}
