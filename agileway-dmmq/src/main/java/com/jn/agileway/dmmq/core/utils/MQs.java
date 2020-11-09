package com.jn.agileway.dmmq.core.utils;

import com.lmax.disruptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MQs {
    public static final Map<String, WaitStrategy> builtinWaitStrategyMap;

    static {
        HashMap<String, WaitStrategy> map = new HashMap<String, WaitStrategy>();
        map.put("blocking", new BlockingWaitStrategy());
        map.put("sleeping", new SleepingWaitStrategy());
        map.put("yielding", new YieldingWaitStrategy());
        map.put("busySpin", new BusySpinWaitStrategy());

        builtinWaitStrategyMap = Collections.unmodifiableMap(map);
    }
}
