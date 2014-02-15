package com.trcooke.breakerbreaker;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StackFailureCountingCircuitBreakerTest {
    public static final int FAILURE_COUNT_THRESHOLD = 2;
    public static final int TIMEOUT_IN_SECONDS = 2;
    CircuitBreaker circuitBreaker = new StackFailureCountingCircuitBreaker(FAILURE_COUNT_THRESHOLD, TIMEOUT_IN_SECONDS);

    @Test
    public void givenInitialised_ThenShouldBeClosed() {
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    public void givenThresholdFailuresNotReached_ThenShouldBeClosed() {
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    public void givenThresholdFailuresReached_ThenShouldBeOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    public void givenFailure_FollowedBySuccess_FollowedByFailure_ThenShouldBeClosed() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerSuccess();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    public void givenCircuitOpen_WhenSuccessRegistered_ThenCircuitOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        circuitBreaker.registerSuccess();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    public void givenSuccess_FollowedByTwoFailures_ThenShouldBeOpen() {
        circuitBreaker.registerSuccess();
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    public void givenThresholdFailuresReached_AndTimeoutExpired_ThenShouldBeHalfOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        long timeAfterDoubleTimeout = System.currentTimeMillis() + (TIMEOUT_IN_SECONDS * 2000);
        BreakerState breakerState;
        do {
            breakerState = circuitBreaker.getState();
            if ((System.currentTimeMillis() > timeAfterDoubleTimeout) && breakerState == BreakerState.OPEN) {
                fail("Breaker should be Half Open by now.");
            }
        } while (breakerState == BreakerState.OPEN);
        assertThat(breakerState, is(BreakerState.HALF_OPEN));
    }

    @Test
    public void givenThresholdFailuresReached_AndTimeoutNotExpired_ThenShouldBeOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        long timeAfterHalfTimeout = System.currentTimeMillis() + (TIMEOUT_IN_SECONDS * 500);
        while (System.currentTimeMillis() < timeAfterHalfTimeout);
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }
}
