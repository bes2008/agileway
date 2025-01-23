package com.jn.agileway.shell.test.runmode;

import com.jn.agileway.shell.Shell;
import org.junit.Test;

import static com.jn.agileway.shell.test.TestUtils.newInteractiveShell;

public class RunModeTests {
    @Test
    public void test(){
        Shell shell = newInteractiveShell();
        shell.startup();
    }
}
