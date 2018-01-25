package com.trcooke.breakerbreaker.time;


import org.junit.jupiter.api.Test;

import static com.trcooke.breakerbreaker.hamcrestmatcher.IsSystemTimeMillisGiveOrTake.isSystemTimeMillisGiveOrTake;
import static org.hamcrest.MatcherAssert.assertThat;

class SystemTimeTest {

    TimeSource timeSource = new SystemTime();

    @Test
    void shouldGetSystemTimeThatIsNearEnoughThisSystemTime() throws Exception {
        assertThat(timeSource.getTimeMillis(), isSystemTimeMillisGiveOrTake(100L));
    }
}
