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
package com.jn.agileway.metrics.core.collector;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.filter.MetricFilter;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.snapshot.Snapshot;

import java.util.Map;

public class NormalMetricsCollector extends MetricsCollector {

    NormalMetricsCollector(Map<String, String> globalTags, double rateFactor,
                           double durationFactor, MetricFilter filter) {
        super(globalTags, rateFactor, durationFactor, filter);
    }

    @Override
    public void collect(MetricName name, Timer timer, long timestamp) {
        final Snapshot snapshot = timer.getSnapshot();

        this.addMetric(name, "count", timer.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                .addMetric(name, "m1", convertRate(timer.getM1Rate()), timestamp)
                .addMetric(name, "m5", convertRate(timer.getM5Rate()), timestamp)
                .addMetric(name, "m15", convertRate(timer.getM15Rate()), timestamp)
                // convert duration
                .addMetric(name, "max", convertDuration(snapshot.getMax()), timestamp)
                .addMetric(name, "min", convertDuration(snapshot.getMin()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "stddev", convertDuration(snapshot.getStdDev()), timestamp)
                .addMetric(name, "median", convertDuration(snapshot.getMedian()), timestamp)
                .addMetric(name, "p75", convertDuration(snapshot.get75thPercentile()), timestamp)
                .addMetric(name, "p95", convertDuration(snapshot.get95thPercentile()), timestamp)
                .addMetric(name, "p99", convertDuration(snapshot.get99thPercentile()), timestamp);

        // instant count
        addInstantCountMetric(timer.getInstantCount(), name, timer.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Histogram histogram, long timestamp) {
        final Snapshot snapshot = histogram.getSnapshot();
        this.addMetric(name, "count", histogram.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                .addMetric(name, "max", snapshot.getMax(), timestamp)
                .addMetric(name, "min", snapshot.getMin(), timestamp)
                .addMetric(name, "mean", snapshot.getMean(), timestamp)
                .addMetric(name, "stddev", snapshot.getStdDev(), timestamp)
                .addMetric(name, "median", snapshot.getMedian(), timestamp)
                .addMetric(name, "p75", snapshot.get75thPercentile(), timestamp)
                .addMetric(name, "p95", snapshot.get95thPercentile(), timestamp)
                .addMetric(name, "p99", snapshot.get99thPercentile(), timestamp);
    }

    @Override
    public void collect(MetricName name, Compass compass, long timestamp) {
        final Snapshot snapshot = compass.getSnapshot();
        this.addMetric(name, "count", compass.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(compass.getM1Rate()), timestamp)
                .addMetric(name, "m5", convertRate(compass.getM5Rate()), timestamp)
                .addMetric(name, "m15", convertRate(compass.getM15Rate()), timestamp)
                // convert duration
                .addMetric(name, "max", convertDuration(snapshot.getMax()), timestamp)
                .addMetric(name, "min", convertDuration(snapshot.getMin()), timestamp)
                .addMetric(name, "mean", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "rt", convertDuration(snapshot.getMean()), timestamp)
                .addMetric(name, "stddev", convertDuration(snapshot.getStdDev()), timestamp)
                .addMetric(name, "median", convertDuration(snapshot.getMedian()), timestamp)
                .addMetric(name, "p75", convertDuration(snapshot.get75thPercentile()), timestamp)
                .addMetric(name, "p95", convertDuration(snapshot.get95thPercentile()), timestamp)
                .addMetric(name, "p99", convertDuration(snapshot.get99thPercentile()), timestamp);

        // instant count
        addInstantCountMetric(compass.getInstantCount(), name, compass.getInstantCountInterval(), timestamp);

        // instant success count
        addInstantSuccessCount(name, compass, timestamp);

        // instant error code count
        addCompassErrorCode(name, compass, timestamp);

        // instant addon count
        addAddonMetric(name, compass, timestamp);
    }

    @Override
    public void collect(MetricName name, Meter meter, long timestamp) {
        this.addMetric(name, "count", meter.getCount(), timestamp, MetricObject.MetricType.COUNTER)
                // convert rate
                .addMetric(name, "m1", convertRate(meter.getM1Rate()), timestamp)
                .addMetric(name, "m5", convertRate(meter.getM5Rate()), timestamp)
                .addMetric(name, "m15", convertRate(meter.getM15Rate()), timestamp);

        // instant count
        addInstantCountMetric(meter.getInstantCount(), name, meter.getInstantCountInterval(), timestamp);
    }

    @Override
    public void collect(MetricName name, Counter counter, long timestamp) {
        MetricName normalizedName = name.getKey().endsWith("count") ? name : name.resolve("count");
        this.addMetric(normalizedName, counter.getCount(), timestamp, MetricObject.MetricType.COUNTER,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));

        if (counter instanceof BucketCounter) {
            int countInterval = ((BucketCounter) counter).getBucketInterval();
            // bucket count
            addInstantCountMetric(((BucketCounter) counter).getBucketCounts(), name, countInterval, timestamp);
        }
    }

    @Override
    public void collect(MetricName name, Gauge gauge, long timestamp) {
        this.addMetric(name, gauge.getValue(), timestamp, MetricObject.MetricType.GAUGE,
                metricsCollectPeriodConfig.period(name.getMetricLevel()));
    }
}