package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.agileway.metrics.core.meter.Histogram;

public class NoopHistogram implements Histogram {
    public static final NoopHistogram NOOP_HISTOGRAM = new NoopHistogram();
    @Override
    public void update(int value) {
    }

    @Override
    public void update(long value) {
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public Snapshot getSnapshot() {
        return NoopCompass.NOOP_COMPASS.getSnapshot();
    }

    @Override
    public long lastUpdateTime() {
        return 0;
    }
}
