package com.backend.backend.exception;

/**
 * Exception thrown when attempting to modify a resource after its grace period has expired.
 * Used primarily for diagnostics that can only be modified within a configurable time window.
 */
public class GracePeriodExpiredException extends RuntimeException {

    private final int gracePeriodMinutes;

    public GracePeriodExpiredException(String entityType, int gracePeriodMinutes) {
        super(String.format("Cannot modify %s: grace period of %d minutes has expired.", entityType, gracePeriodMinutes));
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    public GracePeriodExpiredException(String message) {
        super(message);
        this.gracePeriodMinutes = 0;
    }

    public int getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }
}

