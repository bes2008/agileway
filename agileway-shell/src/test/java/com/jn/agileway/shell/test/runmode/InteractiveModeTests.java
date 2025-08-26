package com.jn.agileway.shell.test.runmode;

import com.jn.agileway.shell.Shell;

import static com.jn.agileway.shell.test.TestUtils.newInteractiveShell;

public class InteractiveModeTests {
    public static void main(String[] args) {
        Shell shell = newInteractiveShell();
        shell.startup();
    }

}
