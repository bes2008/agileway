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
        adhocTest("env-variables JAVA");
        adhocTest("env-variables");

        adhocTest("env variables JAVA");
    }

    @Test
    public void testSystemProperties(){
        adhocTest("system-props         ");
        adhocTest("system-props");
        adhocTest("system-props -i");
        adhocTest("system-props -iv");

        adhocTest("system-props Jav");
        adhocTest("system-props -i Jav");
        adhocTest("system-props -v Jav");
        adhocTest("system-props -i -v Jav");
        adhocTest("system-props -iv Jav");
    }

    private void adhocTest(String cmdline) {
        System.out.println("cmdline: " + cmdline + "\t\t ===>");
        newAdhocShell().start(ShellCmdlines.cmdlineToArgs(cmdline));
    }
}
