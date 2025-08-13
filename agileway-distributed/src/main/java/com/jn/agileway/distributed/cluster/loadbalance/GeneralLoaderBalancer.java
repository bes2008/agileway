package com.jn.agileway.distributed.cluster.loadbalance;

import java.util.List;

public class GeneralLoaderBalancer<INVOCATION> implements LoadBalancer<INVOCATION> {
    private AvailableNodeListProvider provider;
    private LoadBalanceStrategy<INVOCATION> loadBalanceStrategy;

    public void setProvider(AvailableNodeListProvider provider) {
        this.provider = provider;
    }

    public void setLoadBalanceStrategy(LoadBalanceStrategy<INVOCATION> loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    @Override
    public Node select(INVOCATION invocation) {
        List<Node> reachableNodes = provider.get(null);
        return loadBalanceStrategy.select(reachableNodes, invocation);
    }
}
