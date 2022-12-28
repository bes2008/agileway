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
    private String rateUnitString;
    private String durationUnitString;

    private MetricMeterPredicate predicate;

    BaseMetricOutput(MetricMeterPredicate predicate, TimeUnit durationUnit, TimeUnit rateUnit) {
        this.durationUnit = durationUnit;
        this.rateUnit = rateUnit;
        this.predicate = predicate;
        this.rateFactor = rateUnit.toSeconds(1);
        this.durationFactor = 1.0 / durationUnit.toNanos(1);
        rateUnitString = calculateRateUnit(rateUnit);
        durationUnitString = durationUnit.toString().toLowerCase(Locale.US);
    }

    private String calculateRateUnit(TimeUnit unit) {
        final String s = unit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }

    public TimeUnit getDurationUnit() {
        return durationUnit;
    }


    public TimeUnit getRateUnit() {
        return rateUnit;
    }

    protected String getRateUnitString() {
        return rateUnitString;
    }

    protected String getDurationUnitString() {
        return durationUnitString;
    }


    protected double convertDuration(double duration) {
        return duration * durationFactor;
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }

    @Override
    public void write(MetricMeterRegistry registry) {
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
