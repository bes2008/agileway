package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.meter.FastCompass;
import com.jn.langx.util.Emptys;

import java.util.Map;

/**
 * @since 4.1.0
 */
public class NoopFastCompass implements FastCompass {
    public static final NoopFastCompass NOOP_FAST_COMPASS = new NoopFastCompass();
    @Override
    public void record(long duration, String subCategory) {

    }

    @Override
    public Map<String, Map<Long, Long>> getMethodCountPerCategory() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodRtPerCategory() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodCountPerCategory(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<String, Map<Long, Long>> getMethodRtPerCategory(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public int getBucketInterval() {
        return 0;
    }

    @Override
    public Map<String, Map<Long, Long>> getCountAndRtPerCategory() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<String, Map<Long, Long>> getCountAndRtPerCategory(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }
}
