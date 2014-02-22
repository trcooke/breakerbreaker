package com.trcooke.breakerbreaker;

import com.trcooke.breakerbreaker.exceptions.BreakerOpenException;

public interface CircuitBreaker {

    void registerFailure();

    void registerSuccess();

    BreakerState getState();

    void checkAvailable() throws BreakerOpenException;
}