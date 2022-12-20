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

import java.util.Map;

/**
 * A dynamic metric set.
 * The metrics inside will change dynamically.
 */
public interface DynamicMetricSet extends Metric {

    /**
     * A map of metric names to metrics.
     * The metrics inside will change dynamically.
     * So DO NOT register them at first time.
     *
     * @return the dynamically changing metrics
     */
    Map<MetricName, Metric> getDynamicMetrics();
}
