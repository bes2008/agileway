package com.jn.agileway.metrics.core;

import com.jn.agileway.metrics.core.meter.*;
import com.jn.agileway.metrics.core.meter.impl.ClusterHistogram;
import com.jn.agileway.metrics.core.metricset.MetricFactory;
import com.jn.agileway.metrics.core.metricset.MetricFactoryBinder;
import com.jn.agileway.metrics.core.noop.NoopMetricFactory;
import com.jn.agileway.metrics.core.snapshot.ReservoirType;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

/**
 * @since 4.1.0
 */
public class Metrics {
    private Metrics() {
    }

    /**
     * A special number to represent an invalid data
     * 一个特殊的数字来代表一个无效的结果
     */
    public static long NOT_AVAILABLE = -10001L;


    /**
     * Shorthand method for backwards compatibility in creating metric names.
     * <p>
     * Uses {@link MetricName#build(String...)} for its
     * heavy lifting.
     *
     * @param name  The first element of the name
     * @param names The remaining elements of the name
     * @return A metric name matching the specified components.
     * @see MetricName#build(String...)
     */
    public static MetricName name(String name, String... names) {
        final int length;

        if (names == null) {
            length = 0;
        } else {
            length = names.length;
        }

        final String[] parts = new String[length + 1];
        parts[0] = name;

        for (int i = 0; i < length; i++) {
            parts[i + 1] = names[i];
        }

        return MetricName.build(parts);
    }
    // ******************** start static method ************************

    /**
     * @see #name(String, String...)
     */
    public static MetricName name(Class<?> klass, String... names) {
        return name(klass.getName(), names);
    }

    public static final MetricFactory NOOP_METRIC_FACTORY = new NoopMetricFactory();
    private static final String BINDER_CLASS = Reflects.getFQNClassName(MetricFactoryBinder.class);
    private static volatile MetricFactory metricFactory;

    /**
     * Create a {@link Meter} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Meter实例，如果不存在则会创建
     * Meter(计量器)主要用于统计调用qps, 包括最近1min, 5min, 15min的移动平均qps
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of meter
     */
    public static Meter getMeter(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getMeter(group, name);
    }

    /**
     * Create a {@link Counter} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Counter实例，如果不存在则会创建
     * Counter(计数器), 主要用于用于计数，支持+1, -1, +n, -n等操作
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of counter
     */
    public static Counter getCounter(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getCounter(group, name);
    }

    /**
     * Create a {@link Histogram} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Histogram实例，如果不存在则会创建
     * Histogram(直方图), 主要用于统计分布情况，例如调用rt分布，
     * 能够迅速了解统计指标的最大值，最小值，平均值，方差，70%,85%,95%分位数等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of histogram
     */
    public static Histogram getHistogram(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getHistogram(group, name);
    }

    /**
     * Create a {@link Histogram} metric in given group, name, and {@link ReservoirType} type.
     * if not exist, an instance will be created.
     * 根据给定的group和name和type, 获取一个Histogram实例，如果不存在则会创建
     * Histogram(直方图), 主要用于统计分布情况，例如调用rt分布，
     * 能够迅速了解统计指标的最大值，最小值，平均值，方差，70%，85%，95%分位数等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @param type  the type of the {@link ReservoirType}
     * @return an instance of histogram
     */
    public static Histogram getHistogram(String group, MetricName name, ReservoirType type) {
        MetricFactory factory = getMetricFactory();
        return factory.getHistogram(group, name, type);
    }

    /**
     * Create a {@link Timer} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Timer实例，如果不存在则会创建
     * Timer(计时器), 主要用于给定指标的qps, rt分布, 可以理解为Meter+Histogram
     * 能够方便的统计某指标的qps, 和rt的最大值，最小值，平均值，方差，70%,85%,95%分位数等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of timer
     */
    public static Timer getTimer(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getTimer(group, name);
    }

    /**
     * Create a {@link Timer} metric in given group, name, and {@link ReservoirType} type.
     * if not exist, an instance will be created.
     * 根据给定的group, name, type, 获取一个Timer实例，如果不存在则会创建
     * Timer(计时器), 主要用于给定指标的qps, rt分布, 可以理解为Meter+Histogram
     * 能够方便的统计某指标的qps, 和rt的最大值，最小值，平均值，方差，70%, 85%, 95%分位数等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @param type  the type of reservoir
     * @return an instance of timer
     */
    public static Timer getTimer(String group, MetricName name, ReservoirType type) {
        MetricFactory factory = getMetricFactory();
        return factory.getTimer(group, name, type);
    }

    /**
     * Create a {@link Compass} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Compass实例，如果不存在则会创建
     * Compass(罗盘), 主要用于统计给定指标的qps, rt分布，调用成功次数，以及错误码分布等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of compass
     */
    public static Compass getCompass(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getCompass(group, name);
    }

    /**
     * Create a {@link Compass} metric in given group, name, and {@link ReservoirType} type.
     * if not exist, an instance will be created.
     * 根据给定的group, name和type, 获取一个Compass实例，如果不存在则会创建
     * Compass(罗盘), 主要用于统计给定指标的qps, rt分布，调用成功次数，以及错误码分布等信息
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of compass
     */
    public static Compass getCompass(String group, MetricName name, ReservoirType type) {
        MetricFactory factory = getMetricFactory();
        return factory.getCompass(group, name, type);
    }

    /**
     * Create a {@link FastCompass} metric in given group, and name
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个{@link FastCompass}实例，如果不存在则会创建
     * {@link FastCompass}, 主要用于在高吞吐率场景下，统计给定指标的qps, 平均rt，成功率，以及错误码等指标
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of {@link FastCompass}
     */
    public static FastCompass getFastCompass(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getFastCompass(group, name);
    }

    /**
     * Create a {@link ClusterHistogram} metric in given group, and name
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个{@link ClusterHistogram}实例，如果不存在则会创建
     * {@link ClusterHistogram}, 主要用于集群分位数统计
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of {@link ClusterHistogram}
     */
    public static ClusterHistogram getClusterHistogram(String group, MetricName name, long[] buckets) {
        MetricFactory factory = getMetricFactory();
        return factory.getClusterHistogram(group, name, buckets);
    }

    /**
     * Create a {@link ClusterHistogram} metric in given group, and name
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个{@link ClusterHistogram}实例，如果不存在则会创建
     * {@link ClusterHistogram}, 主要用于集群分位数统计
     *
     * @param group the group of MetricRegistry
     * @param name  the name of the metric
     * @return an instance of {@link ClusterHistogram}
     */
    public static ClusterHistogram getClusterHistogram(String group, MetricName name) {
        MetricFactory factory = getMetricFactory();
        return factory.getClusterHistogram(group, name, null);
    }


    /**
     * Register a customized metric to specified group.
     *
     * @param group  the group name of MetricRegistry
     * @param metric the metric to register
     */
    public static void register(String group, MetricName name, Metric metric) {
        MetricFactory factory = getMetricFactory();
        factory.register(group, name, metric);
    }

    /**
     * get dynamically bound {@link MetricFactory} instance
     *
     * @return the {@link MetricFactory} instance bound
     */
    @SuppressWarnings("unchecked")
    public static MetricFactory getMetricFactory() {
        if (metricFactory == null) {
            synchronized (Metrics.class) {
                if (metricFactory == null) {
                    try {
                        Class binderClazz = Metrics.class.getClassLoader().loadClass(BINDER_CLASS);
                        Method getSingleton = binderClazz.getMethod("getSingleton");
                        Object binderObject = getSingleton.invoke(null);
                        Method getMetricManager = binderClazz.getMethod("getMetricFactory");
                        metricFactory = (MetricFactory) getMetricManager.invoke(binderObject);
                    } catch (Exception e) {
                        metricFactory = NOOP_METRIC_FACTORY;
                    }
                }
            }
        }
        return metricFactory;
    }

}
