package com.trcooke.breakerbreaker.time;

public class SystemTime implements TimeSource {
    @Override
    public long getTimeMillis() {
        return System.currentTimeMillis();
    }
}
