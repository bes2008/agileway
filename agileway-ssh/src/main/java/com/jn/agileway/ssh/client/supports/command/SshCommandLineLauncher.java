package com.jn.agileway.ssh.client.supports.command;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.launcher.CommandLauncher;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SshCommandLineLauncher implements CommandLauncher<SshCommandExecutionAdaptor> {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandLineLauncher.class);
    private SshConnection connection;

    public SshCommandLineLauncher(SshConnection connection) {
        this.connection = connection;
    }

    @Override
    public SshCommandExecutionAdaptor exec(CommandLine commandLine, Map<String, String> env) throws IOException {
        return exec(commandLine, env, null);
    }

    @Override
    public SshCommandExecutionAdaptor exec(CommandLine commandLine, Map<String, String> env, File workingDirectory) throws IOException {
        try {
            if (!connection.isConnected()) {
                throw new SshException(new IllegalStateException("connection is not connected"));
            }

            SessionedChannel sessionChannel = connection.openSession();
            Preconditions.checkNotNull(sessionChannel, "the ssh exec session channel is null");
            String command = commandLine.getCommandLineString();

            if (workingDirectory != null) {
                String path = workingDirectory.getPath();
                path = path.replace("\\", "/");
                command = "cd " + path + ";" + command;
            }

            sessionChannel.exec(command);
            return new SshCommandExecutionAdaptor(sessionChannel);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public boolean isFailure(int i) {
        return false;
    }
}
