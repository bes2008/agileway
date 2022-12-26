package com.jn.agileway.metrics.core.metricset;


import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricPredicate;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;

import java.util.Map;
import java.util.Set;

/**
 * A registry of metric instances.
 *
 * @since 4.1.0
 */
public interface MetricRegistry extends MetricSet {


    // ******************** end static method ************************


    /**
     * Given a {@link Meter}, registers it under the given name.
     *
     * @param name   the name of the metric
     * @param metric the metric
     * @param <T>    the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     */
    <T extends Meter> T register(String name, T metric) throws IllegalArgumentException;

    /**
     * Given a {@link Meter}, registers it under the given name.
     *
     * @param name   the name of the metric
     * @param metric the metric
     * @param <T>    the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     */
    <T extends Meter> T register(MetricName name, T metric) throws IllegalArgumentException;

    /**
     * Given a metric set, registers them.
     *
     * @param metrics a set of metrics
     * @throws IllegalArgumentException if any of the names are already registered
     */
    void registerAll(MetricSet metrics) throws IllegalArgumentException;

    /**
     * Creates a new {@link Counter} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Counter}
     */
    Counter counter(String name);

    /**
     * Return the {@link Counter} registered under this name; or create and register
     * a new {@link Counter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    Counter counter(MetricName name);

    /**
     * Return the {@link Histogram} registered under this name; or create and register
     * a new {@link Histogram} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    Histogram histogram(MetricName name);

    /**
     * Create a histogram with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a histogram instance
     */
    Histogram histogram(MetricName name, ReservoirType type);

    /**
     * Creates a new {@link Histogram} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Histogram}
     */
    Histogram histogram(String name);

    /**
     * Return the {@link Metered} registered under this name; or create and register
     * a new {@link Metered} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Metered}
     */
    Metered meter(MetricName name);

    /**
     * Creates a new {@link Metered} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Metered}
     */
    Metered meter(String name);

    /**
     * Return the {@link Timer} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    Timer timer(MetricName name);

    /**
     * Create a timer with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a timer instance
     */
    Timer timer(MetricName name, ReservoirType type);

    /**
     * Creates a new {@link Timer} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Timer}
     */
    Timer timer(String name);

    /**
     * Return the {@link Compass} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Compass}
     */
    Compass compass(MetricName name);

    /**
     * Create a compass with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a compass instance
     */
    Compass compass(MetricName name, ReservoirType type);

    /**
     * Create a FastCompass with given name
     *
     * @param name the name of the metric
     * @return a FastCompass instance
     */
    FastCompass fastCompass(MetricName name);

    /**
     * Creates a new {@link Compass} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Compass}
     */
    Compass compass(String name);


    /**
     * Creates a new {@link ClusterHistogram} and registers it under the given name.
     *
     * @param name    the name of the metric
     * @param buckets the array of long values for buckets
     * @return a new {@link ClusterHistogram}
     */
    ClusterHistogram clusterHistogram(MetricName name, long[] buckets);


    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    boolean remove(MetricName name);

    /**
     * Removes all metrics which match the given filter.
     *
     * @param filter a filter
     */
    void removeMatching(MetricPredicate filter);

    /**
     * Adds a {@link MetricRegistryListener} to a collection of listeners that will be notified on
     * metric creation.  Listeners will be notified in the order in which they are added.
     * <p/>
     * <b>N.B.:</b> The listener will be notified of all existing metrics when it first registers.
     *
     * @param listener the listener that will be notified
     */
    void addListener(MetricRegistryListener listener);

    /**
     * Removes a {@link MetricRegistryListener} from this registry's collection of listeners.
     *
     * @param listener the listener that will be removed
     */
    void removeListener(MetricRegistryListener listener);

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    Set<MetricName> getNames();

    /**
     * Returns a map of all the gauges in the registry and their names.
     *
     * @return all the gauges in the registry
     */
    Map<MetricName, Gauge> getGauges();

    /**
     * Returns a map of all the gauges in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the gauges in the registry
     */
    Map<MetricName, Gauge> getGauges(MetricPredicate filter);

    /**
     * Returns a map of all the counters in the registry and their names.
     *
     * @return all the counters in the registry
     */
    Map<MetricName, Counter> getCounters();

    /**
     * Returns a map of all the counters in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the counters in the registry
     */
    Map<MetricName, Counter> getCounters(MetricPredicate filter);

    /**
     * Returns a map of all the histograms in the registry and their names.
     *
     * @return all the histograms in the registry
     */
    Map<MetricName, Histogram> getHistograms();

    /**
     * Returns a map of all the histograms in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the histograms in the registry
     */
    Map<MetricName, Histogram> getHistograms(MetricPredicate filter);

    /**
     * Returns a map of all the meters in the registry and their names.
     *
     * @return all the meters in the registry
     */
    Map<MetricName, Metered> getMeters();

    /**
     * Returns a map of all the meters in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the meters in the registry
     */
    Map<MetricName, Metered> getMeters(MetricPredicate filter);

    /**
     * Returns a map of all the timers in the registry and their names.
     *
     * @return all the timers in the registry
     */
    Map<MetricName, Timer> getTimers();

    /**
     * Returns a map of all the timers in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    Map<MetricName, Timer> getTimers(MetricPredicate filter);


    /**
     * Returns a map of all the compasses in the registry and their names.
     *
     * @return all the compasses in the registry
     */
    Map<MetricName, Compass> getCompasses();

    /**
     * Returns a map of all the compasses in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the compasses in the registry
     */
    Map<MetricName, Compass> getCompasses(MetricPredicate filter);

    /**
     * Returns a map of all the compasses in the registry and their names.
     *
     * @return all the compasses in the registry
     */
    Map<MetricName, FastCompass> getFastCompasses();

    /**
     * Returns a map of all the compasses in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the compasses in the registry
     */
    Map<MetricName, FastCompass> getFastCompasses(MetricPredicate filter);

    /**
     * Returns a map of all the {@link ClusterHistogram} in the registry and their names which match the given filter.
     */
    Map<MetricName, ClusterHistogram> getClusterHistograms();

    /**
     * Returns a map of all the {@link ClusterHistogram} in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the {@link ClusterHistogram} in the registry
     */
    Map<MetricName, ClusterHistogram> getClusterHistograms(MetricPredicate filter);


    /**
     * Returns a map of all the metrics in the registry and their names which match the given filter
     *
     * @param filter the metric filter to match
     * @return all the metrics in the registry
     */
    Map<MetricName, Meter> getMetrics(MetricPredicate filter);


}
