package com.jn.agileway.ssh.jsch.exec;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.jsch.ChannelType;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.DefaultCommandLineExecutor;
import com.jn.langx.commandline.InstructionSequence;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JschCommandLineExecutor extends DefaultCommandLineExecutor {
    private Session session;

    public JschCommandLineExecutor() {
        this(null, null);
    }

    public JschCommandLineExecutor(Session session) {
        this(null, session);
    }

    public JschCommandLineExecutor(File workingDirectory, Session session) {
        setWorkingDirectory(Objs.useValueIfNull(workingDirectory, new File("~")));
        setSession(session);
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    protected InstructionSequence launch(CommandLine commandLine, Map<String, String> env, File dir) throws IOException {
        try {
            if (!session.isConnected()) {
                session.connect();
            }

            ChannelExec channel = (ChannelExec) session.openChannel(ChannelType.EXEC.getName());
            String command = commandLine.getExecutable();
            channel.setCommand(command);
            channel.connect();

            return new ChannelExecAdaptor(channel);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }
}
