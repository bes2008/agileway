package com.jn.agileway.distributed.distributed.cluster.loadbalance;

public interface Weighter<NODE extends Node, INVOCATION> {
    int getWeight(NODE node, INVOCATION invocation);
}
