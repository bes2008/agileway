package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.meter.Counter;

public class NoopCounter implements Counter {
    public static final NoopCounter NOOP_COUNTER = new NoopCounter();

    @Override
    public void inc() {
    }

    @Override
    public void inc(long n) {
    }

    @Override
    public void dec() {
    }

    @Override
    public void dec(long n) {
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }
};