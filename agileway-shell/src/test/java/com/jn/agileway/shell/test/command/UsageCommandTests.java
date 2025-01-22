package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.RunMode;
import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.agileway.shell.cmdline.ShellCmdlines;
import org.junit.Test;

public class UsageCommandTests {
    private Shell newAdhocShell() {
        return new ShellBuilder()
                .defaultRunMode(RunMode.ADHOC)
                .build();
    }

    @Test
    public void testCommands() {
        adhocTest("commands -g builtin");
        adhocTest("commands -g builtin2");
        adhocTest("commands --groups builtin3");
        adhocTest("commands builtin builtin2");
        adhocTest("commands");
        adhocTest("commands -a builtin");
        adhocTest("commands -a builtin2");
        adhocTest("commands -a");
    }

    private void adhocTest(String cmdline) {
        System.out.println("cmdline: " + cmdline + "\t\t ===>");
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs(cmdline));
    }
}
