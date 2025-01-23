package com.jn.agileway.shell.test.command;

import org.junit.Test;

import static com.jn.agileway.shell.test.TestUtils.adhocTest;

public class DiagnosisCommandTests {
    @Test
    public void testStacktrace(){
        for (int i=0;i< 15;i++) {
            adhocTest("stacktrace -2"+i);
        }
        adhocTest("stacktrace");
        adhocTest("stacktrace -30");
        adhocTest("stacktrace -1");
    }
}
