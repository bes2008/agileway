package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

@SuppressWarnings("ALL")
public abstract class AbstractLoadBalanceStrategy<NODE extends Node, INVOCATION> implements LoadBalanceStrategy<NODE, INVOCATION> {
    private String name;
    @Nullable
    private Weighter<NODE, INVOCATION> weighter;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setWeighter(Weighter<NODE, INVOCATION> weighter) {
        this.weighter = weighter;
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

        if (Emptys.isEmpty(reachableNodes)) {
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
