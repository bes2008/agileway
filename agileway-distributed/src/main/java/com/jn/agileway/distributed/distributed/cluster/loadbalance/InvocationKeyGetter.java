package com.jn.agileway.distributed.distributed.cluster.loadbalance;

public interface InvocationKeyGetter<NODE extends Node, INVOCATION> {
    String get(NODE node, INVOCATION invocation);
}
