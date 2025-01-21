package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.RunMode;
import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.agileway.shell.cmdline.ShellCmdlines;
import org.junit.Test;

public class UsageCommandTests {
    private Shell newAdhocShell(){
        return new ShellBuilder()
                .defaultRunMode(RunMode.ADHOC)
                .build();
    }
    @Test
    public void testCommands(){
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("commands -g builtin2"));
    }
}
