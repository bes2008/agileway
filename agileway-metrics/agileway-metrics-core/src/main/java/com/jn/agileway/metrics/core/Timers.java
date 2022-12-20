package com.jn.agileway.metrics.core;


import com.jn.agileway.metrics.core.annotation.Timed;

public class Timers {
    /**
     * Start a timing sample.
     * @param clock a clock to be used
     * @return A timing sample with start time recorded.
     */
    static Timer.Sample start(com.jn.agileway.metrics.core.Clock clock) {
        return new Timer.Sample(clock);
    }

    public static Timer.Builder builder(String name) {
        return new Timer.Builder(name);
    }

    /**
     * @param registry A meter registry against which the timer will be registered.
     * @param name The name of the timer.
     * @return A timing builder that automatically records a timing on close.
     * @since 1.6.0
     */
    static Timer.ResourceSample resource(com.jn.agileway.metrics.core.MeterRegistry registry, String name) {
        return new Timer.ResourceSample(registry, name);
    }

    /**
     * Create a timer builder from a {@link Timed} annotation.
     * @param timed The annotation instance to base a new timer on.
     * @param defaultName A default name to use in the event that the value attribute is
     * empty.
     * @return This builder.
     */
    static Timer.Builder builder(Timed timed, String defaultName) {
        if (timed.longTask() && timed.value().isEmpty()) {
            // the user MUST name long task timers, we don't lump them in with regular
            // timers with the same name
            throw new IllegalArgumentException(
                    "Long tasks instrumented with @Timed require the value attribute to be non-empty");
        }

        return new Timer.Builder(timed.value().isEmpty() ? defaultName : timed.value()).tags(timed.extraTags())
                .description(timed.description().isEmpty() ? null : timed.description())
                .publishPercentileHistogram(timed.histogram())
                .publishPercentiles(timed.percentiles().length > 0 ? timed.percentiles() : null);
    }

    /**
     * Start a timing sample using the {@link com.jn.agileway.metrics.core.Clock#SYSTEM System clock}.
     * @return A timing sample with start time recorded.
     * @since 1.1.0
     */
    static Timer.Sample start() {
        return start(com.jn.agileway.metrics.core.Clock.SYSTEM);
    }

    /**
     * Start a timing sample.
     * @param registry A meter registry whose clock is to be used
     * @return A timing sample with start time recorded.
     */
    static Timer.Sample start(com.jn.agileway.metrics.core.MeterRegistry registry) {
        return start(registry.config().clock());
    }
}
