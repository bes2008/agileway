package com.jn.agileway.metrics.core.predicate;

import com.jn.agileway.metrics.core.Meter;
import com.jn.agileway.metrics.core.Metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 4.1.0
 */
public class CompositeMetricPredicate implements MetricPredicate {

    MetricPredicate[] predicates;

    /**
     * 如果包含 {@link FixedPredicate#TRUE} 则直接丢弃，减少一次无谓的判断
     *
     * @param predicates
     */
    public CompositeMetricPredicate(MetricPredicate... predicates) {
        List<MetricPredicate> predicateList = new ArrayList<MetricPredicate>(Arrays.asList(predicates));
        predicateList.remove(FixedPredicate.TRUE);
        if (!predicateList.isEmpty()) {
            this.predicates = predicateList.toArray(new MetricPredicate[predicateList.size()]);
        }
    }

    @Override
    public boolean test(Metric name, Meter metric) {
        if (predicates != null) {
            for (MetricPredicate predicate : predicates) {
                if (!predicate.test(name, metric)) {
                    return false;
                }
            }
        }
        return true;
    }


}
