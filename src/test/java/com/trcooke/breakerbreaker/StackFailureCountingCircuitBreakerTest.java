package com.trcooke.breakerbreaker;

import com.trcooke.breakerbreaker.time.TimeSource;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StackFailureCountingCircuitBreakerTest {
    private static final int FAILURE_COUNT_THRESHOLD = 2;
    private static final int TIMEOUT_IN_SECONDS = 2;
    private static final long TIMEOUT_IN_MILLIS = TIMEOUT_IN_SECONDS * 1000;
    private TimeSource timeSource = mock(TimeSource.class);
    private CircuitBreaker circuitBreaker = new StackFailureCountingCircuitBreaker(FAILURE_COUNT_THRESHOLD, TIMEOUT_IN_SECONDS, timeSource);

    @Test
    void givenInitialised_ThenShouldBeClosed() {
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    void givenThresholdFailuresNotReached_ThenShouldBeClosed() {
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    void givenThresholdFailuresReached_ThenShouldBeOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    void givenFailure_FollowedBySuccess_FollowedByFailure_ThenShouldBeClosed() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerSuccess();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }

    @Test
    void givenCircuitOpen_WhenSuccessRegistered_ThenCircuitOpen() {
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        circuitBreaker.registerSuccess();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    void givenSuccess_FollowedByTwoFailures_ThenShouldBeOpen() {
        circuitBreaker.registerSuccess();
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    void givenThresholdFailuresReached_AndTimeoutExpired_ThenShouldBeHalfOpen() {
        when(timeSource.getTimeMillis())
                .thenReturn(0L)
                .thenReturn(TIMEOUT_IN_MILLIS);
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.HALF_OPEN));
    }

    @Test
    void givenThresholdFailuresReached_AndTimeoutNotExpired_ThenShouldBeOpen() {
        when(timeSource.getTimeMillis())
                .thenReturn(0L)
                .thenReturn(TIMEOUT_IN_MILLIS - 1);
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    void givenThresholdFailuresReached_AndTimeoutExpired_WhenRegisterFailure_ThenShouldBeOpen() {
        when(timeSource.getTimeMillis())
                .thenReturn(0L)
                .thenReturn(TIMEOUT_IN_MILLIS);
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        assertThat(circuitBreaker.getState(), is(BreakerState.OPEN));
    }

    @Test
    void givenThresholdFailuresReached_AndTimeoutExpired_WhenRegisterSuccess_ThenShouldBeClosed() {
        when(timeSource.getTimeMillis())
                .thenReturn(0L)
                .thenReturn(TIMEOUT_IN_MILLIS);
        circuitBreaker.registerFailure();
        circuitBreaker.registerFailure();
        circuitBreaker.registerSuccess();
        assertThat(circuitBreaker.getState(), is(BreakerState.CLOSED));
    }
}
