package com.jn.agileway.distributed.cluster.loadbalance;

public interface LoadBalancerAware {
    LoadBalancer getLoadBalancer();

    void setLoadBalancer(LoadBalancer loadBalancer);
}
