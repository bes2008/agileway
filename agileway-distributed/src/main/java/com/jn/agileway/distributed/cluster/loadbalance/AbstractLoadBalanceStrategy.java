package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

@SuppressWarnings("ALL")
public abstract class AbstractLoadBalanceStrategy<NODE extends Node, INVOCATION> implements LoadBalanceStrategy<NODE, INVOCATION> {
    private String name;
    @Nullable
    private Weighter weighter;
    private LoadBalancer<NODE, INVOCATION> loadBalancer;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setWeighter(Weighter weighter) {
        this.weighter = weighter;
    }

    @Override
    public LoadBalancer<NODE, INVOCATION> getLoadBalancer() {
        return loadBalancer;
    }

    @SuppressWarnings("unchecked")
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * 获取node的权重
     */
    public int getWeight(NODE node, INVOCATION invocation) {
        if (weighter != null) {
            return weighter.getWeight(node, invocation);
        }
        return 0;
    }

    protected abstract NODE doSelect(List<NODE> reachableNodes, INVOCATION invocation);

    @Override
    public NODE select(List<NODE> reachableNodes, INVOCATION invocation) {
        // 过滤掉没有注册的 node
        reachableNodes = Pipeline.of(reachableNodes).filter(new Predicate<NODE>() {
            @Override
            public boolean test(NODE node) {
                return getLoadBalancer().hasNode(node);
            }
        }).asList();

        if (Emptys.isEmpty(reachableNodes) || getLoadBalancer().isEmpty()) {
            Logger logger = Loggers.getLogger(getClass());
            logger.warn("Can't find any reachable nodes");
            return null;
        }

        if (reachableNodes.size() == 1) {
            return reachableNodes.get(0);
        }
        return doSelect(reachableNodes, invocation);
    }
}
