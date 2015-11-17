package com.trcooke.breakerbreaker;

import com.trcooke.breakerbreaker.exceptions.BreakerOpenException;
import com.trcooke.breakerbreaker.time.TimeSource;

public class StackFailureCountingCircuitBreaker implements CircuitBreaker {
    private int failureCount = 0;
    private BreakerState breakerState = BreakerState.CLOSED;
    private int failureCountThreshold;
    private int timeoutInSeconds;
    private TimeSource timeSource;
    private long lastOpened;

    StackFailureCountingCircuitBreaker(int failureCountThreshold, int timeoutInSeconds, TimeSource timeSource) {
        this.failureCountThreshold = failureCountThreshold;
        this.timeoutInSeconds = timeoutInSeconds;
        this.timeSource = timeSource;
    }

    @Override
    public void registerFailure() {
        this.failureCount++;
        if (failureCount >= failureCountThreshold) {
            breakerState = BreakerState.OPEN;
            lastOpened = timeSource.getTimeMillis();
        }
    }

    @Override
    public void registerSuccess() {
        checkTimeout();
        switch (breakerState) {
            case CLOSED: {
                if (failureCount > 0) {
                    this.failureCount--;
                }
                break;
            }
            case HALF_OPEN: {
                reset();
                break;
            }
            default: break;
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

    @Override
    public void checkAvailable() throws BreakerOpenException {
        if (breakerState == BreakerState.OPEN) {
            throw new BreakerOpenException();
        }
    }

    private void checkTimeout() {
        if (breakerState == BreakerState.OPEN && timeoutExpired()) {
            breakerState = BreakerState.HALF_OPEN;
        }
    }

    private boolean timeoutExpired() {
        return (timeSource.getTimeMillis() >= lastOpened + (timeoutInSeconds * 1000));
    }

}
