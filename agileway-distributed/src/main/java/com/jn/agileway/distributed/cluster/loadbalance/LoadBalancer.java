package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.util.function.Predicate;

import java.util.List;

public interface LoadBalancer<NODE extends Node, INVOCATION> {
    List<NODE> getNodes();

    List<NODE> getNodes(Predicate<NODE> predicate);

    NODE select(INVOCATION invocation);
}
