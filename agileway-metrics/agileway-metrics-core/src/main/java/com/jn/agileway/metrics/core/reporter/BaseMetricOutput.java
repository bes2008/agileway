package com.jn.agileway.metrics.core.reporter;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meterset.MetricMeterRegistry;
import com.jn.agileway.metrics.core.predicate.MetricMeterPredicate;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseMetricOutput implements MetricOutput {
    private double durationFactor;
    private TimeUnit durationUnit;
    private double rateFactor;

    private TimeUnit rateUnit;

    private MetricMeterPredicate predicate;

    public void setPredicate(MetricMeterPredicate predicate) {
        this.predicate = predicate;
    }

    public void setDurationFactor(double durationFactor) {
        this.durationFactor = durationFactor;
    }

    public void setRateFactor(double rateFactor) {
        this.rateFactor = rateFactor;
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(TimeUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public TimeUnit getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(TimeUnit rateUnit) {
        this.rateUnit = rateUnit;
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

    @Override
    public void write(MetricMeterRegistry registry, MetricMeterPredicate predicate) {
        writeGauges(registry.getGauges(predicate));
        writeCounters(registry.getCounters(predicate));
        writeCompasses(registry.getCompasses(predicate));
        writeFastCompasses(registry.getFastCompasses(predicate));
        writeHistograms(registry.getHistograms(predicate));
        writeMetereds(registry.getMetereds(predicate));
        writeTimers(registry.getTimers(predicate));
    }

    protected void writeMetrics(Map<Metric, ? extends Meter> metrics) {
    }

    protected void writeGauges(Map<Metric, Gauge> metrics) {
        writeMetrics(metrics);
    }

    protected void writeCounters(Map<Metric, Counter> metrics) {
        writeMetrics(metrics);
    }

    protected void writeCompasses(Map<Metric, Compass> metrics) {
        writeMetrics(metrics);
    }

    protected void writeFastCompasses(Map<Metric, FastCompass> metrics) {
        writeMetrics(metrics);
    }

    protected void writeHistograms(Map<Metric, Histogram> metrics) {
        writeMetrics(metrics);
    }

    protected void writeTimers(Map<Metric, Timer> metrics) {
        writeMetrics(metrics);
    }

    protected void writeMetereds(Map<Metric, Metered> metrics) {
        writeMetrics(metrics);
    }
}
