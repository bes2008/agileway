package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.langx.util.Emptys;

import java.util.Map;

public class NoopClusterHistogram extends ClusterHistogram {
    public static final NoopClusterHistogram NOOP_CLUSTER_HISTOGRAM = new NoopClusterHistogram();
    @Override
    public void update(long value) {

    }

    @Override
    public Map<Long, Map<Long, Long>> getBucketValues(long startTime) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }
}
