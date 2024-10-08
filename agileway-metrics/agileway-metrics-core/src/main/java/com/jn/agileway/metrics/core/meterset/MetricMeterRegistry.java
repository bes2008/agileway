package com.jn.agileway.metrics.core.meterset;


import com.jn.agileway.metrics.core.*;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
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
public interface MetricMeterRegistry extends MetricMeterSet {


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
    <T extends Meter> T register(Metric name, T metric) throws IllegalArgumentException;

    /**
     * Given a metric set, registers them.
     *
     * @param metrics a set of metrics
     * @throws IllegalArgumentException if any of the names are already registered
     */
    void registerAll(MetricMeterSet metrics) throws IllegalArgumentException;

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
    Counter counter(Metric name);

    /**
     * Return the {@link Histogram} registered under this name; or create and register
     * a new {@link Histogram} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    Histogram histogram(Metric name);

    /**
     * Create a histogram with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a histogram instance
     */
    Histogram histogram(Metric name, ReservoirType type);

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
    Metered metered(Metric name);

    /**
     * Creates a new {@link Metered} and registers it under the given name.
     *
     * @param name the name of the metric
     * @return a new {@link Metered}
     */
    Metered metered(String name);

    /**
     * Return the {@link Timer} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    Timer timer(Metric name);

    /**
     * Create a timer with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a timer instance
     */
    Timer timer(Metric name, ReservoirType type);

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
    Compass compass(Metric name);

    /**
     * Create a compass with given name, and reservoir type
     *
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a compass instance
     */
    Compass compass(Metric name, ReservoirType type);

    /**
     * Create a FastCompass with given name
     *
     * @param name the name of the metric
     * @return a FastCompass instance
     */
    FastCompass fastCompass(Metric name);

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
    ClusterHistogram clusterHistogram(Metric name, long[] buckets);


    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    boolean remove(Metric name);

    /**
     * Removes all metrics which match the given filter.
     *
     * @param filter a filter
     */
    void removeMatching(MetricMeterPredicate filter);

    /**
     * Adds a {@link MetricMeterRegistryListener} to a collection of listeners that will be notified on
     * metric creation.  Listeners will be notified in the order in which they are added.
     * <p/>
     * <b>N.B.:</b> The listener will be notified of all existing metrics when it first registers.
     *
     * @param listener the listener that will be notified
     */
    void addListener(MetricMeterRegistryListener listener);

    /**
     * Removes a {@link MetricMeterRegistryListener} from this registry's collection of listeners.
     *
     * @param listener the listener that will be removed
     */
    void removeListener(MetricMeterRegistryListener listener);

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    Set<Metric> getNames();

    /**
     * Returns a map of all the gauges in the registry and their names.
     *
     * @return all the gauges in the registry
     */
    Map<Metric, Gauge> getGauges();

    /**
     * Returns a map of all the gauges in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the gauges in the registry
     */
    Map<Metric, Gauge> getGauges(MetricMeterPredicate filter);

    /**
     * Returns a map of all the counters in the registry and their names.
     *
     * @return all the counters in the registry
     */
    Map<Metric, Counter> getCounters();

    /**
     * Returns a map of all the counters in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the counters in the registry
     */
    Map<Metric, Counter> getCounters(MetricMeterPredicate filter);

    /**
     * Returns a map of all the histograms in the registry and their names.
     *
     * @return all the histograms in the registry
     */
    Map<Metric, Histogram> getHistograms();

    /**
     * Returns a map of all the histograms in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the histograms in the registry
     */
    Map<Metric, Histogram> getHistograms(MetricMeterPredicate filter);

    /**
     * Returns a map of all the meters in the registry and their names.
     *
     * @return all the meters in the registry
     */
    Map<Metric, Metered> getMetereds();

    /**
     * Returns a map of all the meters in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the meters in the registry
     */
    Map<Metric, Metered> getMetereds(MetricMeterPredicate filter);

    /**
     * Returns a map of all the timers in the registry and their names.
     *
     * @return all the timers in the registry
     */
    Map<Metric, Timer> getTimers();

    /**
     * Returns a map of all the timers in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    Map<Metric, Timer> getTimers(MetricMeterPredicate filter);


    /**
     * Returns a map of all the compasses in the registry and their names.
     *
     * @return all the compasses in the registry
     */
    Map<Metric, Compass> getCompasses();

    /**
     * Returns a map of all the compasses in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the compasses in the registry
     */
    Map<Metric, Compass> getCompasses(MetricMeterPredicate filter);

    /**
     * Returns a map of all the compasses in the registry and their names.
     *
     * @return all the compasses in the registry
     */
    Map<Metric, FastCompass> getFastCompasses();

    /**
     * Returns a map of all the compasses in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the compasses in the registry
     */
    Map<Metric, FastCompass> getFastCompasses(MetricMeterPredicate filter);

    /**
     * Returns a map of all the {@link ClusterHistogram} in the registry and their names which match the given filter.
     */
    Map<Metric, ClusterHistogram> getClusterHistograms();

    /**
     * Returns a map of all the {@link ClusterHistogram} in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the {@link ClusterHistogram} in the registry
     */
    Map<Metric, ClusterHistogram> getClusterHistograms(MetricMeterPredicate filter);


    /**
     * Returns a map of all the metrics in the registry and their names which match the given filter
     *
     * @param filter the metric filter to match
     * @return all the metrics in the registry
     */
    Map<Metric, Meter> getMetrics(MetricMeterPredicate filter);


}
