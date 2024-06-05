package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.AbstarctSessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.util.Emptys;
import com.sshtools.j2ssh.session.SessionChannelClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

class J2sshSessionedChannel extends AbstarctSessionedChannel {
    private SessionChannelClient sessionChannelClient;

    J2sshSessionedChannel(SessionChannelClient sessionChannelClient) {
        this.sessionChannelClient = sessionChannelClient;
    }

    @Override
    public void pty(String term) throws SshException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException {
        String termModes = null;
        if (Emptys.isNotEmpty(terminalModes)) {
            termModes = new String(PTYMode.encode(terminalModes));
        }
        try {
            sessionChannelClient.requestPseudoTerminal(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, termModes);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalX11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            sessionChannelClient.requestX11Forwarding(x11ScreenNumber, x11AuthenticationCookie);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }

    }

    @Override
    public void env(String variableName, String variableValue) throws SshException {
        try {
            sessionChannelClient.setEnvironmentVariable(variableName, variableValue);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalExec(String command) throws SshException {
        try {
            sessionChannelClient.executeCommand(command);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalSubsystem(String subsystem) throws SshException {
        try {
            sessionChannelClient.startSubsystem(subsystem);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalShell() throws SshException {
        try {
            sessionChannelClient.startShell();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void signal(Signal signal) throws SshException {
    }

    @Override
    public int getExitStatus() {
        long maxWait = 5000;
        Integer exitStatus = sessionChannelClient.getExitCode();
        while (exitStatus == null && maxWait > 0) {
            try {
                int timeout = 10;
                maxWait = maxWait - timeout;
                synchronized (this) {
                    wait(timeout);
                }
            } catch (Throwable ex) {
                // ignore it
            }

            exitStatus = sessionChannelClient.getExitCode();
        }
        return exitStatus == null ? 0 : exitStatus;
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        try {
            return sessionChannelClient.getStderrInputStream();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }


    @Override
    public InputStream getInputStream() throws SshException {
        return sessionChannelClient.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        return sessionChannelClient.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        sessionChannelClient.close();
    }
}
