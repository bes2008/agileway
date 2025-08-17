package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.Provider;

import java.util.List;

public interface NodeListProvider<NODE extends Node> extends Provider<String, List<NODE>> {
    List<NODE> get(String key);
}
