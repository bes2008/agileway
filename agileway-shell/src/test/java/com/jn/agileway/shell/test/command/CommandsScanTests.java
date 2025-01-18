package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
import com.jn.langx.util.Strings;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandsScanTests {
    private static Shell shell;

    @BeforeClass
    public static void initShell(){
        shell = new ShellBuilder()
                .build();
    }

    @Test
    public void testEnvVariables(){
        shell.start(Strings.split("env-variables"," "));
        shell.start(Strings.split("system-props"," "));
        shell.start(Strings.split("system props -a"," "));
    }
}
