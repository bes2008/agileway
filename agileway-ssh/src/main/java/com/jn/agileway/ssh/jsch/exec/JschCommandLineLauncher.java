package com.jn.agileway.ssh.jsch.exec;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.jsch.ChannelType;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.launcher.CommandLauncher;
import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JschCommandLineLauncher implements CommandLauncher<ChannelExecAdaptor> {
    private static final Logger logger = LoggerFactory.getLogger(JschCommandLineLauncher.class);
    private Session session;

    public JschCommandLineLauncher(Session session) {
        this.session = session;
    }

    @Override
    public ChannelExecAdaptor exec(CommandLine commandLine, Map<String, String> env) throws IOException {
        return exec(commandLine, env, null);
    }

    @Override
    public ChannelExecAdaptor exec(CommandLine commandLine, Map<String, String> env, File workingDirectory) throws IOException {
        try {
            if (!session.isConnected()) {
                session.connect();
            }

            ChannelExec channel = (ChannelExec) session.openChannel(ChannelType.EXEC.getName());
            String command = commandLine.getCommandLineString();

            if (workingDirectory != null) {
                String path = workingDirectory.getPath();
                path = path.replace("\\","/");
                command = "cd " + path + ";" + command;
            }

            channel.setCommand(command);
            channel.connect();
            logger.debug("isClosed: {}", channel.isClosed());
            logger.debug("isConnected: {}", channel.isConnected());
            return new ChannelExecAdaptor(channel);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public boolean isFailure(int i) {
        return false;
    }
}
