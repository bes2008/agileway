package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.RunMode;
import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.agileway.shell.ShellLines;
import org.junit.Test;

public class CommandsScanTests {
    private Shell newAdhocShell(){
        return new ShellBuilder()
                .defaultRunMode(RunMode.ADHOC)
                .build();
    }

    @Test
    public void testEnvVariables(){
        newAdhocShell().start(ShellLines.cmdlineToArgs("env-variables -s JAVA"));
        newAdhocShell().start(ShellLines.cmdlineToArgs("env-variables"));
        newAdhocShell().start(ShellLines.cmdlineToArgs("system-props -s user"));
        newAdhocShell().start(ShellLines.cmdlineToArgs("system-props -a"));
        newAdhocShell().start(ShellLines.cmdlineToArgs("system-props"));
        newAdhocShell().start(ShellLines.cmdlineToArgs("env variables -s JAVA"));
    }
}
