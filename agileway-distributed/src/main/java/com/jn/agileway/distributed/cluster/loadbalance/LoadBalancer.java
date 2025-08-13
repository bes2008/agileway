package com.jn.agileway.distributed.cluster.loadbalance;

public interface LoadBalancer<INVOCATION> {
    Node select(INVOCATION invocation);
}
