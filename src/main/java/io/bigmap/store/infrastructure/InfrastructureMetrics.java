package io.bigmap.store.infrastructure;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Metrics;

import java.util.Map;

public class InfrastructureMetrics {
    private static final String CLEANUP_TIME = "map.cleanup.time";
    private static final String INDEX_SIZE = "map.index.size";

    private final LongTaskTimer cleanupTimer;
    private Gauge indexSizeGauge;

    InfrastructureMetrics() {
        this.cleanupTimer = LongTaskTimer.builder(CLEANUP_TIME)
                .register(Metrics.globalRegistry);
    }

    LongTaskTimer cleanupTimer() {
        return cleanupTimer;
    }

    void meterMapSize(Map toMeter) {
        this.indexSizeGauge = Gauge
                .builder(INDEX_SIZE, toMeter, Map::size)
                .register(Metrics.globalRegistry);
    }
}
