package com.jn.agileway.shell.exec;

import com.jn.langx.environment.Environment;

public interface CommandLineExecutor {
    Environment getEnv();

    CmdExecResult exec(Cmdline cmdline);
}
