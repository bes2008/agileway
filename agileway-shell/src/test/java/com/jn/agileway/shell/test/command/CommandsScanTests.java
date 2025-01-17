package com.jn.agileway.shell.test.command;

import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.ShellBuilder;
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
        shell.start(new String[]{"env-variables"});
    }
}
