package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.disruptor.utils.DisruptorMQs;
import com.jn.langx.factory.Factory;
import com.lmax.disruptor.WaitStrategy;

public class BuiltinWaitStrategyFactory implements Factory<String, WaitStrategy> {
    @Override
    public WaitStrategy get(String name) {
        return DisruptorMQs.builtinWaitStrategyMap.get(name);
    }

    public boolean isBuiltin(String name) {
        return get(name) != null;
    }
}
