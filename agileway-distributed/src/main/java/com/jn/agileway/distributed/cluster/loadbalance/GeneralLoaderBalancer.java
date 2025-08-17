package com.jn.agileway.distributed.cluster.loadbalance;

import java.util.List;

public class GeneralLoaderBalancer<NODE extends Node, INVOCATION> implements LoadBalancer<NODE, INVOCATION> {
    private AvailableNodeListProvider<NODE> provider;
    private LoadBalanceStrategy<NODE, INVOCATION> loadBalanceStrategy;

    public void setProvider(AvailableNodeListProvider provider) {
        this.provider = provider;
    }

    public void setLoadBalanceStrategy(LoadBalanceStrategy<NODE, INVOCATION> loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    @Override
    public NODE select(INVOCATION invocation) {
        List<NODE> reachableNodes = provider.get(null);
        return loadBalanceStrategy.select(reachableNodes, invocation);
    }
}
