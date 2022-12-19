package com.jn.agileway.metrics.core.utils;


import com.jn.agileway.metrics.core.AbstractMeter;
import com.jn.agileway.metrics.core.Measurement;

import java.util.List;

import static java.util.Collections.emptyList;

public class NoopMeter extends AbstractMeter {

    public NoopMeter(Id id) {
        super(id);
    }

    @Override
    public List<Measurement> measure() {
        return emptyList();
    }

}
