package io.bigmap.store.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

class ApplicationMetrics {

    private final Timer getMethodTimer;
    private final Timer putMethodTimer;
    private final Timer deleteMethodTimer;
    private final Counter putMethodInputBytesCounter;
    private final Counter getMethodOutputBytesCounter;
    private static final String GET_METHOD = "application.map.get.time";
    private static final String PUT_METHOD = "application.map.put.time";
    private static final String DELETE_METHOD = "application.map.delete.time";
    private static final String PUT_METHOD_INPUT = "application.map.put.input.bytes";
    private static final String GET_METHOD_OUTPUT = "application.map.get.output.bytes";

    ApplicationMetrics() {
        this.getMethodTimer = Timer.builder(GET_METHOD)
                .publishPercentiles(0.99, 0.95, 0.90, 0.50)
                .register(Metrics.globalRegistry);
        this.putMethodTimer = Timer.builder(PUT_METHOD)
                .publishPercentiles(0.99, 0.95, 0.90, 0.50)
                .register(Metrics.globalRegistry);
        this.deleteMethodTimer = Timer.builder(DELETE_METHOD)
                .publishPercentiles(0.99, 0.95, 0.90, 0.50)
                .register(Metrics.globalRegistry);
        this.putMethodInputBytesCounter = Counter
                .builder(PUT_METHOD_INPUT)
                .register(Metrics.globalRegistry);
        this.getMethodOutputBytesCounter = Counter
                .builder(GET_METHOD_OUTPUT)
                .register(Metrics.globalRegistry);
    }

    Timer mapGetTimer() {
        return getMethodTimer;
    }

    Timer mapPutTimer() {
        return putMethodTimer;
    }

    Timer mapDeleteTimer() {
        return deleteMethodTimer;
    }

    Counter putMethodInputBytesCounter() {
        return putMethodInputBytesCounter;
    }

    Counter getMethodOutputBytesCounter() {
        return getMethodOutputBytesCounter;
    }
}
