package com.jn.agileway.distributed.cluster.loadbalance;

import java.util.List;

public class AvailableNodeListProvider implements NodeListProvider {
    private NodeListProvider nodeListProvider;

    @Override
    public List<Node> get(String key) {
        return null;
    }
}
