package com.jn.agileway.distributed.cluster.loadbalance;

public class UndefinedInvocationKeyGetter<INVOCATION> implements InvocationKeyGetter<INVOCATION> {
    @Override
    public String get(Node node, INVOCATION invocation) {
        return "undefined";
    }
}
