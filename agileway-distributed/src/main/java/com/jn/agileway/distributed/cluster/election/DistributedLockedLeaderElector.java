package com.jn.agileway.distributed.cluster.election;

import com.jn.agileway.distributed.locks.DistributedLock;

public class DistributedLockedLeaderElector implements LeaderElector {
    private DistributedLock lock;
    private String node;

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }
}
