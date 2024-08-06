package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.AbstarctSessionedChannel;
import com.jn.agileway.ssh.client.channel.ShellExecutor;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

class Ssh2SessionedChannel extends AbstarctSessionedChannel {
    private Session session;

    Ssh2SessionedChannel(@NonNull Session session) {
        Preconditions.checkNotNull(session);
        this.session = session;
    }

    @Override
    public void pty(String term) throws SshException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException {
        byte[] terminalModesBytes = null;
        if (Emptys.isNotEmpty(terminalModes)) {
            terminalModesBytes = PTYMode.encode(terminalModes);
        }
        try {
            this.session.requestPTY(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, terminalModesBytes);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalX11Forwarding(String host, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            this.session.requestX11Forwarding(host, port, Strings.isBlank(x11AuthenticationCookie) ? null : x11AuthenticationCookie.getBytes(Charsets.UTF_8), singleConnection);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void env(String variableName, String variableValue) throws SshException {
        // ganymed-ssh2-1.2.0 is not supports set env variable
    }

    @Override
    protected void internalExec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        try {
            this.session.execCommand(command);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalSubsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        try {
            this.session.startSubSystem(subsystem);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalShell() throws SshException {
        try {
            this.session.startShell();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void signal(Signal signal) throws SshException {
        // unsupported
    }

    @Override
    public int getExitStatus() {
        Integer exitStatus = this.session.getExitStatus();
        if (exitStatus == null) {
            session.waitForCondition(ChannelCondition.EXIT_STATUS, 60000);
            exitStatus = this.session.getExitStatus();
        }
        return exitStatus;
    }


    @Override
    public void close() {
        this.session.close();
    }

    @Override
    public InputStream getInputStream() throws SshException {
        try {
            return session.getStdout();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        try {
            return session.getStdin();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        try {
            return session.getStderr();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
