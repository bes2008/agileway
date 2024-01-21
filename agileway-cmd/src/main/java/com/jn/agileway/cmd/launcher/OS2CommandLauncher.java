package com.jn.agileway.cmd.launcher;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.ProcessAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * A command launcher for OS/2 that uses 'cmd.exe' when launching commands in
 * directories other than the current working directory.
 * <p>
 * Unlike Windows NT and friends, OS/2's cd doesn't support the /d switch to
 * change drives and directories in one go.
 * </p>
 * Please not that this class is currently unused because the Java13CommandLauncher
 * is used for 0S/2
 */
public class OS2CommandLauncher extends CommandLauncherProxy {

    public OS2CommandLauncher(final CommandLauncher launcher) {
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
