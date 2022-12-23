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
package com.jn.agileway.metrics.core;


public interface MetricBuilder<T extends Metric> {

    /**
     * create a new metric instance
     *
     * @param name the name of the metric
     * @return a metric instance
     */
    T newMetric(MetricName name);

    /**
     * check if the current builder can build the given metric
     *
     * @param metric the metric to check
     * @return true if the current builder can build this metric
     */
    boolean isInstance(Metric metric);

    MetricBuilder<T> interval(int interval);

    MetricBuilder<T> newBuilder();
}
