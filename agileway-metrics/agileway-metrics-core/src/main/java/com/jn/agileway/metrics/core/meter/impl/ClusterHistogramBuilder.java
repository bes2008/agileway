/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.agileway.metrics.core.meter.impl;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.metricset.MetricBuilder;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.langx.util.reflect.Reflects;

/**
 * @author wangtao 2019-01-16 10:55
 */
public class ClusterHistogramBuilder extends AbstractMetricBuilder<ClusterHistogram> {

    /**
     * Create a <T extends Metrics> instance with given name and buckets
     *
     * @param name    the name of the metric
     * @param buckets an array of long values
     * @return a metric implementation
     */
    public ClusterHistogram newMetric(MetricName name, long[] buckets){
        return new ClusterHistogramImpl(buckets, this.interval, null);
    }

    @Override
    public ClusterHistogram newMetric(MetricName name) {
        return new ClusterHistogramImpl(this.interval, null);
    }

    @Override
    public boolean isInstance(Metric metric) {
        return Reflects.isInstance(ClusterHistogram.class, metric.getClass());
    }

    @Override
    public MetricBuilder<ClusterHistogram> newBuilder() {
        return new ClusterHistogramBuilder();
    }
}
