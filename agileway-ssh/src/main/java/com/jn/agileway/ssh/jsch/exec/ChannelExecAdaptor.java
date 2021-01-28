package com.jn.agileway.ssh.jsch.exec;

import com.jcraft.jsch.ChannelExec;
import com.jn.langx.commandline.InstructionSequence;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;

import java.io.InputStream;
import java.io.OutputStream;

public class ChannelExecAdaptor implements InstructionSequence {
    private ChannelExec channel;

    public ChannelExecAdaptor(ChannelExec channel) {
        Preconditions.checkNotNull(channel);
        this.channel = channel;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return channel.getOutputStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            return channel.getInputStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public InputStream getErrorStream() {
        try {
            return channel.getErrStream();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    @Override
    public void destroy() {
        channel.disconnect();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }
}
