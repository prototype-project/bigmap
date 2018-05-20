package io.bigmap.store.infrastructure;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Metrics;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InfrastructureMetrics {
    private static final String CLEANUP_TIME = "map.cleanup.time";
    private static final String INDEX_SIZE = "map.index.size";
    private static final String MAP_SIZE_BYTES = "map.size.bytes";
    private static final String NOTIFY_SUCCESS_COUNT = "map.notify.success.count";
    private static final String NOTIFY_FAILURE_COUNT = "map.notify.failure.count";

    private final LongTaskTimer cleanupTimer;
    private final Counter notifySuccessCounter;
    private final Counter notifyFailureCounter;
    private Gauge mapSizeBytesGauge;
    private Gauge indexSizeGauge;

    InfrastructureMetrics() {
        this.cleanupTimer = LongTaskTimer.builder(CLEANUP_TIME)
                .register(Metrics.globalRegistry);
        this.notifySuccessCounter = Counter.builder(NOTIFY_SUCCESS_COUNT)
                .register(Metrics.globalRegistry);
        this.notifyFailureCounter = Counter.builder(NOTIFY_FAILURE_COUNT)
                .register(Metrics.globalRegistry);
    }

    LongTaskTimer cleanupTimer() {
        return cleanupTimer;
    }

    Counter notifySuccessCounter() {
        return notifySuccessCounter;
    }

    Counter notifyFailureCounter() {
        return notifyFailureCounter;
    }

    void meterMapSize(Map toMeter) {
        this.indexSizeGauge = Gauge
                .builder(INDEX_SIZE, toMeter, Map::size)
                .register(Metrics.globalRegistry);
    }

    void meterMapSizeBytes(AtomicLong sizeBytes) {
        this.mapSizeBytesGauge = Gauge
                .builder(MAP_SIZE_BYTES, sizeBytes, AtomicLong::longValue)
                .register(Metrics.globalRegistry);
    }
}
