package com.jn.agileway.distributed.cluster.loadbalance;

public interface InvocationKeyGetter<INVOCATION> {
    String get(Node node, INVOCATION invocation);
}
