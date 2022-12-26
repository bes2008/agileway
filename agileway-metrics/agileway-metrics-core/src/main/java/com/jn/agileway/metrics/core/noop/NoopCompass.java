package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.agileway.metrics.core.meter.BucketCounter;
import com.jn.agileway.metrics.core.meter.Compass;
import com.jn.langx.util.Emptys;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @since 4.1.0
 */
public class NoopCompass implements Compass {

    private static final Compass.Context NOP_COMPASS_CONTEXT = new Compass.Context() {
        @Override
        public long stop() {
            return 0;
        }

        @Override
        public void success() {

        }

        @Override
        public void error(String errorCode) {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void markAddon(String suffix) {

        }
    };


    private static final Snapshot NOP_SNAPSHOT = new Snapshot() {
        @Override
        public double getValue(double quantile) {
            return 0;
        }

        @Override
        public long[] getValues() {
            return new long[0];
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public double getMedian() {
            return 0;
        }

        @Override
        public double get75thPercentile() {
            return 0;
        }

        @Override
        public double get95thPercentile() {
            return 0;
        }

        @Override
        public double get98thPercentile() {
            return 0;
        }

        @Override
        public double get99thPercentile() {
            return 0;
        }

        @Override
        public double get999thPercentile() {
            return 0;
        }

        @Override
        public long getMax() {
            return 0;
        }

        @Override
        public double getMean() {
            return 0;
        }

        @Override
        public long getMin() {
            return 0;
        }

        @Override
        public double getStdDev() {
            return 0;
        }

        @Override
        public void dump(OutputStream output) {
        }
    };

    public static final NoopCompass NOOP_COMPASS = new NoopCompass();

    @Override
    public Map<String, BucketCounter> getErrorCodeCounts() {
        return Collections.emptyMap();
    }


    @Override
    public long getSuccessCount() {
        return 0;
    }

    @Override
    public void update(long duration, TimeUnit unit) {

    }

    @Override
    public void update(long duration, TimeUnit unit, boolean isSuccess, String errorCode, String addon) {

    }

    @Override
    public <T> T time(Callable<T> event) throws Exception {
        return event.call();
    }

    @Override
    public Compass.Context time() {
        return NOP_COMPASS_CONTEXT;
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public double getM15Rate() {
        return 0;
    }

    @Override
    public double getM5Rate() {
        return 0;
    }

    @Override
    public double getMeanRate() {
        return 0;
    }

    @Override
    public double getM1Rate() {
        return 0;
    }

    @Override
    public Snapshot getSnapshot() {
        return NOP_SNAPSHOT;
    }

    @Override
    public Map<Long, Long> getInstantCount() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<String, BucketCounter> getAddonCounts() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public BucketCounter getBucketSuccessCount() {
        return NoopBucketCounter.NOOP_BUCKET_COUNTER;
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