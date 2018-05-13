package io.bigmap.store.application;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

class ApplicationMetrics {

    private final Timer getMethodTimer;
    private final Timer putMethodTimer;
    private final Timer deleteMethodTimer;
    private static final String GET_METHOD = "application.map.get";
    private static final String PUT_METHOD = "application.map.put";
    private static final String DELETE_METHOD = "application.map.delete";

    ApplicationMetrics() {
        this.getMethodTimer = Metrics.timer(GET_METHOD);
        this.putMethodTimer = Metrics.timer(PUT_METHOD);
        this.deleteMethodTimer = Metrics.timer(DELETE_METHOD);
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
}
