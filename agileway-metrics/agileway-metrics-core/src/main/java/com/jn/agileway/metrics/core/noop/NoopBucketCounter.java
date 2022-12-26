package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.meter.BucketCounter;
import com.jn.langx.util.Emptys;

import java.util.Map;

/**
 * @since 4.1.0
 */
public class NoopBucketCounter implements BucketCounter {
    public static NoopBucketCounter NOOP_BUCKET_COUNTER = new NoopBucketCounter();
    @Override
    public void update() {

    }

    @Override
    public void update(long n) {

    }

    @Override
    public Map<Long, Long> getBucketCounts() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Long, Long> getBucketCounts(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public int getBucketInterval() {
        return 0;
    }

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