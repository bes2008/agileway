package com.jn.agileway.distributed.distributed.cluster.loadbalance;

import com.jn.langx.util.function.Predicate;

import java.util.List;

public interface LoadBalancer<NODE extends Node, INVOCATION> {
    void addNode(NODE node);

    void removeNode(NODE node);

    boolean hasNode(NODE node);

    void markDown(NODE node);

    List<NODE> getNodes();

    List<NODE> getNodes(Predicate<NODE> predicate);

    NODE select(INVOCATION invocation);

    boolean isEmpty();
}
