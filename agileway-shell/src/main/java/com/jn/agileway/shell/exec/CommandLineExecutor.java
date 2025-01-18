package com.jn.agileway.shell.exec;

import com.jn.langx.environment.Environment;

import java.lang.reflect.Method;

public class CommandLineExecutor {
    private Environment env;
    public CmdExecResult exec(Cmdline cmdline){
        Method method = cmdline.getCommandDefinition().getMethod();
        return null;
    }
}
