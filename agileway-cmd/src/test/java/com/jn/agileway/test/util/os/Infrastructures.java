package com.jn.agileway.test.util.os;

import com.jn.agileway.test.util.os.hardware.cpu.CPUs;

public class Infrastructures {
    private Infrastructures() {
    }

    public static String getCpuId() {
        return CPUs.getCpuId();
    }
}
