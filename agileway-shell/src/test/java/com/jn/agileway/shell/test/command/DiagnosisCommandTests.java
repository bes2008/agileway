package com.jn.agileway.shell.test.command;

import org.junit.Test;

import static com.jn.agileway.shell.test.TestUtils.adhocTest;

public class DiagnosisCommandTests {
    @Test
    public void testStacktrace(){
        adhocTest("stacktrace");

        adhocTest("stacktrace --help");
    }
}
