package com.jn.agileway.shell.exec;

import com.jn.langx.environment.Environment;

import java.lang.reflect.Method;

public class DefaultCommandLineExecutor implements CommandLineExecutor{
    private Environment env;
    public DefaultCommandLineExecutor(Environment env){
        this.env = env;
    }

    public Environment getEnv() {
        return env;
    }

    public CmdExecResult exec(Cmdline cmdline){
        Method method = cmdline.getCommandDefinition().getMethod();
        return null;
    }
}
