package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.utils.MQs;
import com.jn.langx.Factory;
import com.lmax.disruptor.WaitStrategy;

public class BuiltinWaitStrategyFactory implements Factory<String, WaitStrategy> {
    @Override
    public WaitStrategy get(String name) {
        return MQs.builtinWaitStrategyMap.get(name);
    }

    public boolean isBuiltin(String name) {
        return get(name) != null;
    }
}
