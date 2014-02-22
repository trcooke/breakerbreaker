package com.trcooke.breakerbreaker.time;

import org.junit.Test;

import static com.trcooke.breakerbreaker.hamcrestmatcher.IsSystemTimeMillisGiveOrTake.isSystemTimeMillisGiveOrTake;
import static org.junit.Assert.assertThat;

public class SystemTimeTest {

    TimeSource timeSource = new SystemTime();

    @Test
    public void shouldGetSystemTimeThatIsNearEnoughThisSystemTime() throws Exception {
        assertThat(timeSource.getTimeMillis(), isSystemTimeMillisGiveOrTake(100L));
    }
}
