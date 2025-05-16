package com.jn.agileway.distributed.distributed.cluster.loadbalance;

public interface LoadBalancerAware {
    LoadBalancer getLoadBalancer();

    void setLoadBalancer(LoadBalancer loadBalancer);
}
