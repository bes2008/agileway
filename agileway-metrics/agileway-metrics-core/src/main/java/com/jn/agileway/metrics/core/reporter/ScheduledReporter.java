package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The abstract base class for all scheduled reporters (i.e., reporters which process a registry's
 * metrics periodically).
 *
 * @see ConsoleMetricOutput
 * @see Slf4jReporter
 * @since 4.1.0
 */
public abstract class ScheduledReporter implements Reporter {

    private static final Logger logger = Loggers.getLogger(ScheduledReporter.class);
    private final MetricMeterRegistry registry;
    private final ScheduledExecutorService executor;
    private final MetricMeterPredicate filter;
    private final double durationFactor;
    private final String durationUnit;
    private final double rateFactor;
    private final String rateUnit;
    private MetricOutput output;

    /**
     * Creates a new {@link ScheduledReporter} instance.
     *
     * @param registry     the {@link MetricMeterRegistry} containing the metrics this
     *                     reporter will report
     * @param name         the reporter's name
     * @param filter       the filter for which metrics to report
     * @param rateUnit     a unit of time
     * @param durationUnit a unit of time
     */
    protected ScheduledReporter(MetricMeterRegistry registry,
                                String name,
                                MetricMeterPredicate filter,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit) {
        this(registry, filter, rateUnit, durationUnit,
                Executors.newSingleThreadScheduledExecutor(new CommonThreadFactory(name, true)));
    }

    /**
     * Creates a new {@link ScheduledReporter} instance.
     *
     * @param registry the {@link MetricMeterRegistry} containing the metrics this
     *                 reporter will report
     * @param filter   the filter for which metrics to report
     * @param executor the executor to use while scheduling reporting of metrics.
     */
    protected ScheduledReporter(MetricMeterRegistry registry,
                                MetricMeterPredicate filter,
                                TimeUnit rateUnit,
                                TimeUnit durationUnit,
                                ScheduledExecutorService executor) {
        this.registry = registry;
        this.filter = filter;
        this.executor = executor;
        this.rateFactor = rateUnit.toSeconds(1);
        this.rateUnit = calculateRateUnit(rateUnit);
        this.durationFactor = 1.0 / durationUnit.toNanos(1);
        this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
    }

    /**
     * Starts the reporter polling at the given period.
     *
     * @param period the amount of time between polls
     * @param unit   the unit for {@code period}
     */
    public void start(long period, TimeUnit unit) {
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    report();
                } catch (Throwable ex) {
                    logger.error("Throwable RuntimeException thrown from {}#report. Exception was suppressed.", ScheduledReporter.this.getClass().getSimpleName(), ex);
                }
            }
        }, period, period, unit);
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     * <p>
     * Uses the shutdown pattern from http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
     */
    public void stop() {
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    logger.warn(getClass().getSimpleName() + ": ScheduledExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the reporter and shuts down its thread of execution.
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * Report the current values of all metrics in the registry.
     */
    public void report() {
        synchronized (this) {
            output.write(registry, filter);
        }
    }

    /**
     * Called periodically by the polling thread. Subclasses should report all the given metrics.
     *
     */
    public abstract void report(Map<Metric, Gauge> gauges,
                                Map<Metric, Counter> counters,
                                Map<Metric, Histogram> histograms,
                                Map<Metric, Metered> meters,
                                Map<Metric, Timer> timers);

    protected String getRateUnit() {
        return rateUnit;
    }

    protected String getDurationUnit() {
        return durationUnit;
    }

    protected double convertDuration(double duration) {
        return duration * durationFactor;
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    private String calculateRateUnit(TimeUnit unit) {
        final String s = unit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }


}
