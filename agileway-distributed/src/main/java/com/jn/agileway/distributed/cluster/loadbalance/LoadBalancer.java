package com.jn.agileway.distributed.cluster.loadbalance;

public interface LoadBalancer<NODE extends Node, INVOCATION> {
    NODE select(INVOCATION invocation);
}
