package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.function.Supplier;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class RoundRobinLoadBalanceStrategy<INVOCATION> extends AbstractLoadBalanceStrategy<INVOCATION> {

    public RoundRobinLoadBalanceStrategy() {
        setName("RoundRobin");
    }

    private static final int RECYCLE_PERIOD = 60000;
    private InvocationKeyGetter<INVOCATION> invocationKeyGetter = new UndefinedInvocationKeyGetter<INVOCATION>();

    protected static class WeightedRoundRobin {
        private int weight;
        private final AtomicLong current = new AtomicLong(0L);
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1L * total);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    public void setInvocationKeyGetter(InvocationKeyGetter invocationKeyGetter) {
        if (invocationKeyGetter != null) {
            this.invocationKeyGetter = invocationKeyGetter;
        }
    }

    private ConcurrentMap<String, ConcurrentMap<String, WeightedRoundRobin>> invocationWeightMap = new ConcurrentHashMap<String, ConcurrentMap<String, WeightedRoundRobin>>();


    @Override
    protected Node doSelect(List<Node> reachableNodes, INVOCATION invocation) {
        String key = invocationKeyGetter.get(reachableNodes.get(0), invocation);
        ConcurrentMap<String, WeightedRoundRobin> map = Maps.putIfAbsent(invocationWeightMap, key, new Supplier<String, ConcurrentMap<String, WeightedRoundRobin>>() {
            @Override
            public ConcurrentMap<String, WeightedRoundRobin> get(String k) {
                return new ConcurrentHashMap<String, WeightedRoundRobin>();
            }
        });

        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;
        final long now = System.currentTimeMillis();
        Node selectedNode = null;
        WeightedRoundRobin selectedWRR = null;
        for (Node reachableNode : reachableNodes) {
            String identifyString = reachableNode.getId();
            final int weight = getWeight(reachableNode, invocation);
            WeightedRoundRobin weightedRoundRobin = Maps.putIfAbsent(map, identifyString, new Supplier<String, WeightedRoundRobin>() {
                @Override
                public WeightedRoundRobin get(String k) {
                    WeightedRoundRobin wrr = new WeightedRoundRobin();
                    wrr.setWeight(weight);
                    return wrr;
                }
            });

            if (weight != weightedRoundRobin.getWeight()) {
                //weight changed
                weightedRoundRobin.setWeight(weight);
            }
            long cur = weightedRoundRobin.increaseCurrent();
            weightedRoundRobin.setLastUpdate(now);
            if (cur > maxCurrent) {
                maxCurrent = cur;
                selectedNode = reachableNode;
                selectedWRR = weightedRoundRobin;
            }
            totalWeight += weight;
        }
        if (reachableNodes.size() != map.size()) {
            Collects.removeIf(map, new Predicate2<String, WeightedRoundRobin>() {
                @Override
                public boolean test(String key, WeightedRoundRobin wrr) {
                    return now - wrr.getLastUpdate() > RECYCLE_PERIOD;
                }
            });
        }
        if (selectedNode != null && selectedWRR != null) {
            selectedWRR.sel(totalWeight);
            return selectedNode;
        }
        // should not happen here
        return reachableNodes.get(0);
    }

}
