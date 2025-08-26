package com.jn.agileway.shell.test;

import com.jn.agileway.shell.RunMode;
import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.agileway.shell.cmdline.ShellCmdlines;

public class TestUtils {
    private static Shell newAdhocShell() {
        return new ShellBuilder()
                .defaultRunMode(RunMode.ADHOC)
                .build();
    }

    public static void adhocTest(String cmdline) {
        System.out.println("cmdline: " + cmdline + "\t\t ===>");
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs(cmdline));
    }

    public static Shell newInteractiveShell(){
        return new ShellBuilder()
                .defaultRunMode(RunMode.INTERACTIVE)
                .build();
    }


}
