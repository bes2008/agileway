package com.jn.agileway.metrics.core.meterset;

import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 4.1.0
 */
public interface MetricFactory{

    /**
     * Create a {@link Metered} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of meter
     */
    Metered getMeter(String group, Metric name);

    /**
     * Create a {@link Counter} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of counter
     */
    Counter getCounter(String group, Metric name);

    /**
     * Create a {@link Histogram} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of histogram
     */
    Histogram getHistogram(String group, Metric name);

    /**
     * Create a {@link Histogram} metric in given group, name, and type
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @param type  the type of the reservoir
     * @return an instance of histogram
     */
    Histogram getHistogram(String group, Metric name, ReservoirType type);

    /**
     * Create a {@link Timer} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of timer
     */
    Timer getTimer(String group, Metric name);

    /**
     * Create a {@link Timer} metric in given group, name, and type
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @param type  the type of the reservoir
     * @return an instance of timer
     */
    Timer getTimer(String group, Metric name, ReservoirType type);

    /**
     * Create a {@link Compass} metric in given group, and name.
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of compass
     */
    Compass getCompass(String group, Metric name);

    /**
     * Create a {@link Compass} metric in given group, name, and type
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @param type  the type of the reservoir
     * @return an instance of compass
     */
    Compass getCompass(String group, Metric name, ReservoirType type);

    /**
     * Create a {@link FastCompass} metric in give group, name, and type
     * if not exist, an instance will be created.
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of {@link FastCompass}
     */
    FastCompass getFastCompass(String group, Metric name);


    /**
     * Create a {@link ClusterHistogram} metric in give group, name, and type
     * if not exist, an instance will be created.
     *
     * @param group   the group of MetricRegistry
     * @param name    the name of the metric
     * @param buckets if the buckets is null, a default bucket will be created.
     * @return an instance of {@link ClusterHistogram}
     */
    ClusterHistogram getClusterHistogram(String group, Metric name, long[] buckets);

    /**
     * Register a customized metric to specified group.
     *
     * @param group: the group name of MetricRegistry
     * @param metric the metric to register
     */
    void register(String group, Metric name, Meter metric);

    /**
     * Get a list of group in current factory
     *
     * @return a list of group name
     */
    List<String> listMetricGroups();

    /**
     * A global flag to complete disable the metrics feature
     *
     * @return true if it is enabled.
     */
    boolean isEnabled();

    /**
     * A global flag to complete disable the metrics feature
     */
    void setEnabled(boolean enabled);

    /**
     * list all metric names by group
     *
     * @return a map of metric name set, keyed by group name
     */
    Map<String, Set<Metric>> listMetricNamesByGroup();

    /**
     * Get metric registry by group name,
     * if not found, null will be returned
     *
     * @param group the group name to query
     * @return the MetricRegistry that is correspondent to the group
     */
    MetricRegistry getMetricRegistryByGroup(String group);

    Map<Metric, Gauge> getGauges(String group, MetricPredicate filter);

    Map<Metric, Counter> getCounters(String group, MetricPredicate filter);

    Map<Metric, Histogram> getHistograms(String group, MetricPredicate filter);

    Map<Metric, Metered> getMeters(String group, MetricPredicate filter);

    Map<Metric, Timer> getTimers(String group, MetricPredicate filter);

    Map<Metric, Compass> getCompasses(String group, MetricPredicate filter);

    Map<Metric, FastCompass> getFastCompasses(String group, MetricPredicate filter);

    Map<Metric, ClusterHistogram> getClusterHistogram(String group, MetricPredicate filter);

    /**
     * A map of metric names to metrics.
     *
     * @return the metrics
     */
    Map<Metric, Meter> getMetrics(String group);

    /**
     * 返回不同Metric分类，各自保存好的map
     *
     * @param group
     * @return
     */
    Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group);

    /**
     * 返回不同Metric分类，各自保存好的map
     *
     * @param group
     * @param filter
     * @return
     */
    Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getCategoryMetrics(String group,
                                                                                 MetricPredicate filter);

    /**
     * return all metrics
     *
     * @param filter
     * @return
     */
    Map<Class<? extends Meter>, Map<Metric, ? extends Meter>> getAllCategoryMetrics(MetricPredicate filter);


    void clear();
}
