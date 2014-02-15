package com.trcooke.breakerbreaker;

public class StackFailureCountingCircuitBreaker implements CircuitBreaker {
    private int failureCount = 0;
    private BreakerState breakerState = BreakerState.CLOSED;
    private int failureCountThreshold;
    private int timeoutInSeconds;
    private long lastOpenned;

    StackFailureCountingCircuitBreaker(int failureCountThreshold, int timeoutInSeconds) {
        this.failureCountThreshold = failureCountThreshold;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    @Override
    public void registerFailure() {
        this.failureCount++;
        if (failureCount >= failureCountThreshold) {
            breakerState = BreakerState.OPEN;
            lastOpenned = System.currentTimeMillis();
        }
    }

    @Override
    public void registerSuccess() {
        switch (breakerState) {
            case CLOSED: {
                if (failureCount > 0) {
                    this.failureCount--;
                }
            }
            case HALF_OPEN: {
                reset();
            }
            case OPEN:
        }
    }

    private void reset() {
        failureCount = 0;
        breakerState = BreakerState.CLOSED;
    }

    @Override
    public BreakerState getState() {
        checkTimeout();
        return breakerState;
    }

    private void checkTimeout() {
        if (breakerState == BreakerState.OPEN && timeoutExpired()) {
            breakerState = BreakerState.HALF_OPEN;
        }
    }

    private boolean timeoutExpired() {
        return (System.currentTimeMillis() > lastOpenned + (timeoutInSeconds * 1000));
    }

}
