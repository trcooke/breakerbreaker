package com.trcooke.breakerbreaker;

public interface CircuitBreaker {

    void registerFailure();

    void registerSuccess();

    BreakerState getState();
}