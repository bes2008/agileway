package com.jn.agileway.distributed.cluster.loadbalance;

public interface Weighter<INVOCATION> {
    int getWeight(Node node, INVOCATION invocation);
}
