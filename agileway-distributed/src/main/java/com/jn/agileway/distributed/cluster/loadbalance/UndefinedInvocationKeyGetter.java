package com.jn.agileway.distributed.cluster.loadbalance;

public class UndefinedInvocationKeyGetter<NODE extends Node, INVOCATION> implements InvocationKeyGetter<NODE, INVOCATION> {
    @Override
    public String get(NODE node, INVOCATION invocation) {
        return "undefined";
    }
}
