package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.snapshot.Snapshot;
import com.jn.agileway.metrics.core.meter.Timer;
import com.jn.langx.util.Emptys;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class NoopTimer implements Timer {
    public static final NoopTimer NOOP_TIMER = new NoopTimer();

    private static final Timer.Context NOOP_TIMER_CONTEXT = new Timer.Context() {

        @Override
        public void close() throws IOException {
        }

        @Override
        public long stop() {
            return 0;
        }
    };

    @Override
    public void update(long duration, TimeUnit unit) {
    }

    @Override
    public <T> T time(Callable<T> event) throws Exception {
        return event.call();
    }

    @Override
    public Context time() {
        return NOOP_TIMER_CONTEXT;
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
    public Snapshot getSnapshot() {
        return NoopCompass.NOOP_COMPASS.getSnapshot();
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
}
