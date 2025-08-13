package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.Nameable;
import com.jn.langx.annotation.Nullable;

import java.util.List;

public interface LoadBalanceStrategy<INVOCATION> extends Nameable {
    Node select(List<Node> reachableNodes, @Nullable INVOCATION invocation);
}
