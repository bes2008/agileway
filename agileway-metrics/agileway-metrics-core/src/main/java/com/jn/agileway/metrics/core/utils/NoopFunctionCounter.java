package com.jn.agileway.metrics.core.utils;


import com.jn.agileway.metrics.core.FunctionCounter;

public class NoopFunctionCounter extends NoopMeter implements FunctionCounter {

    public NoopFunctionCounter(Id id) {
        super(id);
    }

    @Override
    public double count() {
        return 0;
    }

}
