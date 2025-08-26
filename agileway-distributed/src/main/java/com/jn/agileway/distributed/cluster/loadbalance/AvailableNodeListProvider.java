package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class AvailableNodeListProvider<NODE extends Node> implements NodeListProvider<NODE> {
    private NodeListProvider<NODE> nodeListProvider;
    private AvailableNodeChecker<NODE> checker;

    public AvailableNodeListProvider(NodeListProvider<NODE> nodeListProvider, AvailableNodeChecker<NODE> checker) {
        this.nodeListProvider = nodeListProvider;
        this.checker = checker;
    }

    @Override
    public List<NODE> get(String key) {
        List<NODE> nodes = nodeListProvider.get(key);
        return Pipeline.of(nodes).filter(new Predicate<NODE>() {
            @Override
            public boolean test(NODE node) {
                return checker.isAvailable(node);
            }
        }).asList();
    }
}
