package com.jn.agileway.metrics.core.predicate;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;
import com.jn.langx.util.collection.Collects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 4.1.0
 */
public class CompositeMetricPredicate implements MetricMeterPredicate {

    MetricMeterPredicate[] predicates;

    /**
     * 如果包含 {@link FixedPredicate#TRUE} 则直接丢弃，减少一次无谓的判断
     *
     * @param predicates
     */
    public CompositeMetricPredicate(MetricMeterPredicate... predicates) {
        List<MetricMeterPredicate> predicateList = new ArrayList<MetricMeterPredicate>(Arrays.asList(predicates));
        predicateList.remove(FixedPredicate.TRUE);
        if (!predicateList.isEmpty()) {
            this.predicates = Collects.toArray(predicateList, MetricMeterPredicate[].class);
        }
    }

    @Override
    public boolean test(Metric name, Meter metric) {
        if (predicates != null) {
            for (MetricMeterPredicate predicate : predicates) {
                if (!predicate.test(name, metric)) {
                    return false;
                }
            }
        }
        return true;
    }


}
