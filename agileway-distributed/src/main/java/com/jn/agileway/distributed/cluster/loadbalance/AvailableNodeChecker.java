package com.jn.agileway.distributed.cluster.loadbalance;

public interface AvailableNodeChecker {
    boolean isAvailable(Node node);
}
