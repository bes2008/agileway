package com.jn.agileway.distributed.cluster.loadbalance;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

@SuppressWarnings("ALL")
public abstract class AbstractLoadBalanceStrategy<INVOCATION> implements LoadBalanceStrategy<INVOCATION> {
    private String name;
    @Nullable
    private Weighter weighter;

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

    /**
     * 获取node的权重
     */
    public int getWeight(Node node, INVOCATION invocation) {
        if (weighter != null) {
            return weighter.getWeight(node, invocation);
        }
        return 0;
    }

    protected abstract Node doSelect(List<Node> reachableNodes, INVOCATION invocation);

    @Override
    public Node select(List<Node> reachableNodes, INVOCATION invocation) {

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
