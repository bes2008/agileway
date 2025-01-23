package com.jn.agileway.shell.test.command;

import org.junit.Test;

import static com.jn.agileway.shell.test.TestUtils.adhocTest;

public class EnvCommandTests {

    @Test
    public void testEnvVariables(){
        adhocTest("env-variables JAVA");
        adhocTest("env-variables");

        adhocTest("env variables JAVA");

        adhocTest("env-variables --help");
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

        adhocTest("system-props --help");
    }
}
