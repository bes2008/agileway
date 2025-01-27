package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.AbstarctSessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.sshtools.client.PseudoTerminalModes;
import com.sshtools.client.SessionChannelNG;
import com.sshtools.common.ssh.RequestFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

class SynergySessionedChannel extends AbstarctSessionedChannel {
    private SessionChannelNG channel;

    SynergySessionedChannel(SessionChannelNG channel) {
        this.channel = channel;
    }

    @Override
    public void pty(String term) throws SshException {
        this.channel.allocatePseudoTerminal(term);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException {
        final PseudoTerminalModes modes = new PseudoTerminalModes();
        if (terminalModes != null) {
            Collects.forEach(terminalModes, new Consumer2<PTYMode, Integer>() {
                @Override
                public void accept(PTYMode ptyMode, Integer value) {
                    try {
                        modes.setTerminalMode(ptyMode.getOpcodeInt(), value);
                    } catch (Throwable ex) {
                        // ignore it
                    }
                }
            });
        }
        this.channel.allocatePseudoTerminal(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, modes);
    }

    @Override
    protected void internalX11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        //
    }


    @Override
    public void env(String variableName, String variableValue) throws SshException {
        try {
            RequestFuture future = channel.setEnvironmentVariable(variableName, variableValue);
            future.waitForever();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalExec(String command) throws SshException {
        try {
            RequestFuture future = channel.executeCommand(command);
            future.waitForever();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalSubsystem(String subsystem) throws SshException {
        try {
            RequestFuture future = channel.startSubsystem(subsystem);
            future.waitForever();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalShell() throws SshException {
        try {
            RequestFuture future = channel.startShell();
            future.waitForever();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void signal(Signal signal) throws SshException {

    }

    @Override
    public int getExitStatus() {
        int exitCode = channel.getExitCode();

        long deadline = System.currentTimeMillis() + 30 * 1000;
        while (exitCode == -2147483648) {
            synchronized (this) {
                if (System.currentTimeMillis() <= deadline) {
                    try {
                        this.wait(50);
                    } catch (InterruptedException ex) {
                        // ignore it
                    }
                }
                exitCode = channel.getExitCode();
            }
        }
        return exitCode;
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        return channel.getStderrStream();
    }

    @Override
    public InputStream getInputStream() throws SshException {
        return channel.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        return channel.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

}
