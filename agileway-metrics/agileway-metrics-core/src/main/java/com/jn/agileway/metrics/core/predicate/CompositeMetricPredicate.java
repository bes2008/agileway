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
package com.jn.agileway.metrics.core.predicate;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeMetricPredicate implements MetricPredicate {

    MetricPredicate[] predicates;

    /**
     * 如果包含 {@link Metrics.Predicates#TRUE} 则直接丢弃，减少一次无谓的判断
     *
     * @param filters
     */
    public CompositeMetricPredicate(MetricPredicate... filters) {
        List<MetricPredicate> filterList = new ArrayList<MetricPredicate>(Arrays.asList(filters));
        filterList.remove(Metrics.Predicates.TRUE);
        if (!filterList.isEmpty()) {
            this.predicates = filterList.toArray(new MetricPredicate[filterList.size()]);
        }
    }

    @Override
    public boolean test(MetricName name, Metric metric) {
        if (predicates != null) {
            for (MetricPredicate filter : predicates) {
                if (!filter.test(name, metric)) {
                    return false;
                }
            }
        }
        return true;
    }


}
