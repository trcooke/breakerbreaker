package com.trcooke.breakerbreaker.hamcrestmatcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsSystemTimeMillisGiveOrTake extends TypeSafeMatcher<Long> {
    private long tolerance;
    private long systemTime = System.currentTimeMillis();

    public IsSystemTimeMillisGiveOrTake(long tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    protected boolean matchesSafely(Long time) {
        return (time >= systemTime - tolerance) && (time <= systemTime + tolerance);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("between <" + (systemTime - tolerance) + "L> and <" + (systemTime + tolerance) + "L>");
    }

    @Factory
    public static Matcher<Long> isSystemTimeMillisGiveOrTake(long tolerance) {
        return new IsSystemTimeMillisGiveOrTake(tolerance);
    }
}
