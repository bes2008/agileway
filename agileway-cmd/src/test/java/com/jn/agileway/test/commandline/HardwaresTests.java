package com.jn.agileway.test.commandline;

import com.jn.agileway.test.util.os.hardware.cpu.CPUs;
import org.junit.jupiter.api.Test;

public class HardwaresTests {

    @Test
    public void testGetCpuId() throws Throwable{
        String id = CPUs.getCpuId();
        System.out.println(id);
    }

}
