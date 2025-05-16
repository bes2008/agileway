package com.jn.agileway.distributed.distributed.cluster.loadbalance;

import com.jn.langx.Nameable;
import com.jn.langx.annotation.Nullable;

import java.util.List;

public interface LoadBalanceStrategy<NODE extends Node, INVOCATION> extends Nameable, LoadBalancerAware{
    NODE select(List<NODE> reachableNodes, @Nullable INVOCATION invocation);
}
