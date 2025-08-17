package com.jn.agileway.distributed.cluster.loadbalance;

public interface AvailableNodeChecker<NODE extends Node> {
    boolean isAvailable(NODE node);
}
