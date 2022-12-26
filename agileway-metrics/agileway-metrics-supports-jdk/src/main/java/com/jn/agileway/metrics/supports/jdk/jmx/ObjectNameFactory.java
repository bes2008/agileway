package com.jn.agileway.metrics.supports.jdk.jmx;

import com.jn.agileway.metrics.core.MetricName;

import javax.management.ObjectName;

/**
 * @since 4.1.0
 */
public interface ObjectNameFactory {

    ObjectName createName(String type, String domain, MetricName name);
}
