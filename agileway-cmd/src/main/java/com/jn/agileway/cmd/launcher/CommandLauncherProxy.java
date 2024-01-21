package com.jn.agileway.cmd.launcher;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.ProcessAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * A command launcher that proxies another command launcher. Sub-classes
 * override exec(args, env, workdir)
 */
public abstract class CommandLauncherProxy extends AbstractLocalCommandLauncher {
    protected final CommandLauncher<ProcessAdapter> myLauncher;

    protected CommandLauncherProxy(final CommandLauncher<ProcessAdapter> launcher) {
        this.myLauncher = launcher;
    }

    /**
     * Launches the given command in a new process. Delegates this method to the
     * proxied launcher
     *
     * @param cmd the command line to execute as an array of strings
     * @param env the environment to set as an array of strings
     * @throws IOException forwarded from the exec method of the command launcher
     */
    @Override
    public ProcessAdapter exec(final CommandLine cmd, final Map<String, String> env, File workDir) throws IOException {
        return myLauncher.exec(cmd, env, null);
    }
}
