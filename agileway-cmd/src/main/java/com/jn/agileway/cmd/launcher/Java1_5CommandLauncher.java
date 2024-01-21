package com.jn.agileway.cmd.launcher;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.ProcessAdapter;
import com.jn.agileway.cmd.environment.EnvironmentUtils;
import com.jn.langx.util.reflect.Reflects;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class Java1_5CommandLauncher extends AbstractLocalCommandLauncher {
    private static final Method RUNTIME_EXEC = Reflects.findMethod(Runtime.class, "exec", String[].class, String[].class, File.class);

    @Override
    public ProcessAdapter exec(CommandLine cmd, Map<String, String> env, File workingDir) throws IOException {
        final String[] envVars = EnvironmentUtils.toStrings(env);
        return ProcessAdapter.of((Process) Reflects.invoke(RUNTIME_EXEC, Runtime.getRuntime(), new Object[]{cmd.toStrings(), envVars, workingDir}, true, true));
    }
}
