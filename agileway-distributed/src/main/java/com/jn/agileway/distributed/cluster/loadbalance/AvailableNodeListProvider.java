package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class AvailableNodeListProvider implements NodeListProvider {
    private NodeListProvider nodeListProvider;
    private AvailableNodeChecker checker;

    public AvailableNodeListProvider(NodeListProvider nodeListProvider, AvailableNodeChecker checker) {
        this.nodeListProvider = nodeListProvider;
        this.checker = checker;
    }

    @Override
    public List<Node> get(String key) {
        List<Node> nodes = nodeListProvider.get(key);
        return Pipeline.of(nodes).filter(new Predicate<Node>() {
            @Override
            public boolean test(Node node) {
                return checker.isAvailable(node);
            }
        }).asList();
    }
}
