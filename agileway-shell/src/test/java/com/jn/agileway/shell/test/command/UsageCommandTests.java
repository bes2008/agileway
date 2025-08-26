package com.jn.agileway.shell.test.command;

import org.junit.Test;

import static com.jn.agileway.shell.test.TestUtils.adhocTest;

public class UsageCommandTests {

    @Test
    public void testCommands() {
        adhocTest("commands -g builtin");
        adhocTest("commands -g builtin2");
        adhocTest("commands --groups builtin3");
        adhocTest("commands builtin builtin2");
        adhocTest("commands");
        adhocTest("commands -as builtin");
        adhocTest("commands -as builtin2");
        adhocTest("commands -a");
    }

    @Test
    public void testHelp(){
        adhocTest("help system-props");
        adhocTest("help env-variables");

        adhocTest("help");

        // test command not found
        adhocTest("help system-props2");
    }
}
