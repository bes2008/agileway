package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistryListener;
import com.jn.agileway.metrics.core.meterset.MetricMeterSet;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.langx.util.Emptys;

import java.util.Map;
import java.util.Set;

/**
 * @since 4.1.0
 */
public class NoopMetricMeterRegistry implements MetricMeterRegistry {
    public static final NoopMetricMeterRegistry NOOP_METRIC_REGISTRY = new NoopMetricMeterRegistry();

    @Override
    public <T extends Meter> T register(String name, T metric) throws IllegalArgumentException {
        return metric;
    }

    @Override
    public <T extends Meter> T register(Metric name, T metric) throws IllegalArgumentException {
        return metric;
    }

    @Override
    public void registerAll(MetricMeterSet metrics) throws IllegalArgumentException {

    }

    @Override
    public Counter counter(String name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Counter counter(Metric name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Histogram histogram(Metric name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram histogram(Metric name, ReservoirType type) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram histogram(String name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Metered meter(Metric name) {
        return NoopMetered.NOOP_METER;
    }

    @Override
    public Metered meter(String name) {
        return NoopMetered.NOOP_METER;
    }

    @Override
    public Timer timer(Metric name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer timer(String name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer timer(Metric name, ReservoirType type) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Compass compass(Metric name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass compass(String name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass compass(Metric name, ReservoirType type) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public FastCompass fastCompass(Metric name) {
        return NoopFastCompass.NOOP_FAST_COMPASS;
    }

    @Override
    public ClusterHistogram clusterHistogram(Metric name, long[] buckets) {
        return NoopClusterHistogram.NOOP_CLUSTER_HISTOGRAM;
    }

    @Override
    public boolean remove(Metric name) {
        return false;
    }

    @Override
    public void removeMatching(MetricMeterPredicate filter) {

    }

    @Override
    public void addListener(MetricMeterRegistryListener listener) {

    }

    @Override
    public void removeListener(MetricMeterRegistryListener listener) {

    }

    @Override
    public Set<Metric> getNames() {
        return Emptys.EMPTY_TREE_SET;
    }

    @Override
    public Map<Metric, Gauge> getGauges() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Gauge> getGauges(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Counter> getCounters() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Counter> getCounters(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Histogram> getHistograms() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Histogram> getHistograms(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Metered> getMeters() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Metered> getMeters(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Timer> getTimers() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Timer> getTimers(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Compass> getCompasses(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Compass> getCompasses() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, FastCompass> getFastCompasses() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, FastCompass> getFastCompasses(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, ClusterHistogram> getClusterHistograms(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Meter> getMetrics(MetricMeterPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, Meter> getMetricMeters() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<Metric, ClusterHistogram> getClusterHistograms() {
        return Emptys.EMPTY_TREE_MAP;
    }
}
