package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.meter.Meter;
import com.jn.langx.util.Emptys;

import java.util.Map;

public class NoopMeter implements Meter {
    public static final NoopMeter NOOP_METER = new NoopMeter();

    @Override
    public void mark() {
    }

    @Override
    public void mark(long n) {
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public double getFifteenMinuteRate() {
        return 0;
    }

    @Override
    public double getFiveMinuteRate() {
        return 0;
    }

    @Override
    public double getMeanRate() {
        return 0;
    }

    @Override
    public double getOneMinuteRate() {
        return 0;
    }

    @Override
    public Map<Long, Long> getInstantCount() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public int getInstantCountInterval() {
        return 0;
    }

    @Override
    public Map<Long, Long> getInstantCount(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }
};