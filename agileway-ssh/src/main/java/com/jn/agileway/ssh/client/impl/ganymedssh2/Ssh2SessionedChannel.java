package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.Session;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Ssh2SessionedChannel implements SessionedChannel {
    private Session session;

    public Ssh2SessionedChannel(@NonNull Session session) {
        Preconditions.checkNotNull(session);
        this.session = session;
    }

    @Override
    public void pty(String term) throws IOException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, byte[] terminalModes) throws IOException {
        this.session.requestPTY(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, terminalModes);
    }

    @Override
    public void x11Forwarding(String host, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws IOException {
        this.session.requestX11Forwarding(host, port, Strings.isBlank(x11AuthenticationCookie) ? null : x11AuthenticationCookie.getBytes(Charsets.UTF_8), singleConnection);
    }

    @Override
    public void env(String variableName, String variableValue) {
        // ganymed-ssh2-1.2.0 is not supports set env variable
    }

    @Override
    public void exec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        try {
            this.session.execCommand(command);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void subsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        try {
            this.session.startSubSystem(subsystem);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void shell() throws SshException {
        try {
            this.session.startShell();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void signal(String signal) throws SshException {
        // unsupported
    }

    @Override
    public int getExitStatus() {
        return this.session.getExitStatus();
    }

    @Override
    public String getType() {
        return "session";
    }

    @Override
    public void close() {
        this.session.close();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return session.getStdout();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return session.getStdin();
    }

    @Override
    public InputStream getErrorInputStream() throws IOException {
        return session.getStderr();
    }
}
