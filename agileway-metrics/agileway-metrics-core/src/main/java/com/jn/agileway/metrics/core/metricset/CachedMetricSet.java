/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.agileway.metrics.core.metricset;

import com.jn.langx.util.timing.clock.Clock;
import com.jn.langx.util.timing.clock.Clocks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CachedMetricSet implements MetricSet {

    protected static long DEFAULT_DATA_TTL = 5000;
    // The lock used to collect metric
    private final Object collectLock = new Object();
    // The time (in milli-seconds) to live of cached data
    protected long dataTTL;
    // The last collect time
    protected AtomicLong lastCollectTime;
    // The clock used to calculate time
    protected Clock clock;

    public CachedMetricSet() {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, null);
    }

    public CachedMetricSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, null);
    }

    public CachedMetricSet(long dataTTL, TimeUnit unit, Clock clock) {
        this.dataTTL = unit.toMillis(dataTTL);
        clock = clock==null ? Clocks.defaultClock(): clock;
        this.clock = clock;
        this.lastCollectTime = new AtomicLong(0);
    }

    /**
     * Do not collect data if our cached copy of data is valid.
     * The purpose is to minimize the cost to collect system metric.
     */
    public void refreshIfNecessary() {
        if (clock.getTime() - lastCollectTime.get() > dataTTL) {
            synchronized (collectLock) {
                // double check, in case other thread has already entered.
                if (clock.getTime() - lastCollectTime.get() > dataTTL) {
                    getValueInternal();
                    // update the last collect time stamp
                    lastCollectTime.set(clock.getTime());
                }
            }
        }
    }

    public long lastUpdateTime() {
        return lastCollectTime.get();
    }

    protected abstract void getValueInternal();
}
