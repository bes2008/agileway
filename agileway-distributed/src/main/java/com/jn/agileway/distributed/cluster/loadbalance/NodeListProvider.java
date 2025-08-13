package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.Provider;

import java.util.List;

public interface NodeListProvider extends Provider<String, List<Node>> {
    List<Node> get(String key);
}
