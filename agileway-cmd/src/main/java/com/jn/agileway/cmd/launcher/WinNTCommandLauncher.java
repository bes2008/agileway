package com.jn.agileway.cmd.launcher;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.ProcessAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * A command launcher for Windows XP/2000/NT that uses 'cmd.exe' when launching
 * commands in directories other than the current working directory.
 */
public class WinNTCommandLauncher extends CommandLauncherProxy {
    public WinNTCommandLauncher(final CommandLauncher launcher) {
        super(launcher);
    }

    /**
     * Launches the given command in a new process, in the given working
     * directory.
     *
     * @param cmd        the command line to execute as an array of strings
     * @param env        the environment to set as an array of strings
     * @param workingDir working directory where the command should run
     * @throws IOException forwarded from the exec method of the command launcher
     */
    @Override
    public ProcessAdapter exec(final CommandLine cmd, final Map<String, String> env, final File workingDir) throws IOException {

        final CommandLine newCmd = new CommandLine("cmd");
        newCmd.addArgument("/c");
        newCmd.addArguments(cmd.toStrings());
        return super.exec(newCmd, env, workingDir);
    }
}
