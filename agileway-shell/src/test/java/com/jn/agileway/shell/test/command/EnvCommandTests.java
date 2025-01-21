package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.RunMode;
import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.agileway.shell.cmdline.ShellCmdlines;
import org.junit.Test;

public class EnvCommandTests {
    private Shell newAdhocShell(){
        return new ShellBuilder()
                .defaultRunMode(RunMode.ADHOC)
                .build();
    }

    @Test
    public void testEnvVariables(){
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("env-variables -s JAVA"));
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("env-variables"));

        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("env variables -s JAVA"));
    }

    @Test
    public void testSystemProperties(){
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("system-props -s user"));
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("system-props -a"));
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs("system-props"));
    }
}
