package com.trcooke.breakerbreaker;

import com.trcooke.breakerbreaker.example.Resource;
import com.trcooke.breakerbreaker.exceptions.BreakerOpenException;
import com.trcooke.breakerbreaker.time.SystemTime;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class BreakerUsageExampleTest {

    Resource resource = mock(Resource.class);
    CircuitBreaker circuitBreaker = new StackFailureCountingCircuitBreaker(2, 100, new SystemTime());
    private ResourceAccessorWithBreaker resourceAccessorWithBreaker = new ResourceAccessorWithBreaker(resource, circuitBreaker);

    @Test(expected = BreakerOpenException.class)
    public void givenBreakerWithFailureThresholdOfTwo_WhenThreeConsecutiveFailures_ThenBreakerFailsFastOnThird() throws BreakerOpenException {
        doThrow(new RuntimeException()).when(resource).doStuff();
        try {
            resourceAccessorWithBreaker.askResourceToDoStuff();
            resourceAccessorWithBreaker.askResourceToDoStuff();
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }
        resourceAccessorWithBreaker.askResourceToDoStuff();
    }

    private class ResourceAccessorWithBreaker {
        private Resource resource;
        private CircuitBreaker circuitBreaker;

        public ResourceAccessorWithBreaker(Resource resource, CircuitBreaker circuitBreaker) {
            this.resource = resource;
            this.circuitBreaker = circuitBreaker;
        }

        public void askResourceToDoStuff() throws BreakerOpenException {
            circuitBreaker.checkAvailable();
            try {
                resource.doStuff();
                circuitBreaker.registerSuccess();
            } catch (Exception e) {
                circuitBreaker.registerFailure();
            }
        }
    }
}
