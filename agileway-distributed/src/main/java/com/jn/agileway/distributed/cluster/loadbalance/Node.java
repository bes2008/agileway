package com.jn.agileway.distributed.cluster.loadbalance;

public interface Node {
    String getId();

    String getHost();

    int getPort();
}
