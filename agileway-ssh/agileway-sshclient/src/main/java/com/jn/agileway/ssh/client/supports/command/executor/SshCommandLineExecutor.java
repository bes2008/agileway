package com.jn.agileway.ssh.client.supports.command.executor;

import com.jn.agileway.cmd.DefaultCommandLineExecutor;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.util.Objs;

import java.io.File;

public class SshCommandLineExecutor extends DefaultCommandLineExecutor {

    public SshCommandLineExecutor(SshConnection connection) {
        this(null, connection);
    }

    public SshCommandLineExecutor(File workingDirectory, SshConnection connection) {
        setWorkingDirectory(Objs.useValueIfNull(workingDirectory, new File("~")));
        setLauncher(new SshCommandLineLauncher(connection));
    }

}
