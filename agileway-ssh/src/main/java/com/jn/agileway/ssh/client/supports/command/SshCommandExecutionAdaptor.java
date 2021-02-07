package com.jn.agileway.ssh.client.supports.command;

import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.commandline.InstructionSequence;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;

import java.io.InputStream;
import java.io.OutputStream;

public class SshCommandExecutionAdaptor implements InstructionSequence {
    private SessionedChannel commandChannel;

    public SshCommandExecutionAdaptor(SessionedChannel channel) {
        Preconditions.checkNotNull(channel);
        this.commandChannel = channel;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return commandChannel.getStdOutputStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            return commandChannel.getStdInputStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public InputStream getErrorStream() {
        try {
            return commandChannel.getStdErrorInputStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public void destroy() {
        commandChannel.close();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return commandChannel.getExitStatus();
    }
}
