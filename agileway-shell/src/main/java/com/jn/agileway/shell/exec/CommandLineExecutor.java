package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.result.CmdExecResult;
import com.jn.langx.environment.Environment;
import org.apache.commons.cli.Converter;

public interface CommandLineExecutor {
    Environment getEnv();

    CmdExecResult exec(Cmdline cmdline);

    Converter getConverter();
}
