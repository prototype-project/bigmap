package io.bigmap.common;

public class CriticalError extends Error {
    public CriticalError(String message) {
        super(message);
    }

    public CriticalError(String message, Exception e) {
        super(message, e);
    }

    public CriticalError() {
        super();
    }
}
