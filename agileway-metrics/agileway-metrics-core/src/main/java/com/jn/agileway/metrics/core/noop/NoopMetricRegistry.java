package com.jn.agileway.metrics.core.noop;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.metricset.MetricRegistry;
import com.jn.agileway.metrics.core.metricset.MetricRegistryListener;
import com.jn.agileway.metrics.core.metricset.MetricSet;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.langx.util.Emptys;

import java.util.Map;
import java.util.Set;

/**
 * @since 4.1.0
 */
public class NoopMetricRegistry implements MetricRegistry {
    public static final NoopMetricRegistry NOOP_METRIC_REGISTRY = new NoopMetricRegistry();

    @Override
    public <T extends Meter> T register(String name, T metric) throws IllegalArgumentException {
        return metric;
    }

    @Override
    public <T extends Meter> T register(MetricName name, T metric) throws IllegalArgumentException {
        return metric;
    }

    @Override
    public void registerAll(MetricSet metrics) throws IllegalArgumentException {

    }

    @Override
    public Counter counter(String name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Counter counter(MetricName name) {
        return NoopCounter.NOOP_COUNTER;
    }

    @Override
    public Histogram histogram(MetricName name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram histogram(MetricName name, ReservoirType type) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Histogram histogram(String name) {
        return NoopHistogram.NOOP_HISTOGRAM;
    }

    @Override
    public Metered meter(MetricName name) {
        return NoopMeter.NOOP_METER;
    }

    @Override
    public Metered meter(String name) {
        return NoopMeter.NOOP_METER;
    }

    @Override
    public Timer timer(MetricName name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer timer(String name) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Timer timer(MetricName name, ReservoirType type) {
        return NoopTimer.NOOP_TIMER;
    }

    @Override
    public Compass compass(MetricName name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass compass(String name) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public Compass compass(MetricName name, ReservoirType type) {
        return NoopCompass.NOOP_COMPASS;
    }

    @Override
    public FastCompass fastCompass(MetricName name) {
        return NoopFastCompass.NOOP_FAST_COMPASS;
    }

    @Override
    public ClusterHistogram clusterHistogram(MetricName name, long[] buckets) {
        return NoopClusterHistogram.NOOP_CLUSTER_HISTOGRAM;
    }

    @Override
    public boolean remove(MetricName name) {
        return false;
    }

    @Override
    public void removeMatching(MetricPredicate filter) {

    }

    @Override
    public void addListener(MetricRegistryListener listener) {

    }

    @Override
    public void removeListener(MetricRegistryListener listener) {

    }

    @Override
    public Set<MetricName> getNames() {
        return Emptys.EMPTY_TREE_SET;
    }

    @Override
    public Map<MetricName, Gauge> getGauges() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Gauge> getGauges(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Counter> getCounters() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Counter> getCounters(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Histogram> getHistograms() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Histogram> getHistograms(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Metered> getMeters() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Metered> getMeters(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Timer> getTimers() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Timer> getTimers(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Compass> getCompasses(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Compass> getCompasses() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, FastCompass> getFastCompasses() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, FastCompass> getFastCompasses(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, ClusterHistogram> getClusterHistograms(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Meter> getMetrics(MetricPredicate filter) {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, Meter> getMetrics() {
        return Emptys.EMPTY_TREE_MAP;
    }

    @Override
    public Map<MetricName, ClusterHistogram> getClusterHistograms() {
        return Emptys.EMPTY_TREE_MAP;
    }
}
