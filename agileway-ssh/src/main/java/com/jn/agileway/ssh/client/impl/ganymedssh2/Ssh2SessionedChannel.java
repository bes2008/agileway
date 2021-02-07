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
    public void pty(String term) throws IOException{
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, byte[] terminalModes) throws IOException{
        this.session.requestPTY(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, terminalModes);
    }

    @Override
    public void x11Forwarding(String host, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber)throws IOException {
        this.session.requestX11Forwarding(host, port, Strings.isBlank(x11AuthenticationCookie)?null:x11AuthenticationCookie.getBytes(Charsets.UTF_8), singleConnection);
    }

    @Override
    public void env(String variableName, String variableValue) {

    }

    @Override
    public void exec(String command) throws SshException {

    }

    @Override
    public void subsystem(String subsystem) throws SshException {

    }

    @Override
    public void shell() throws SshException {

    }

    @Override
    public void signal(String signal) throws SshException {

    }

    @Override
    public int getExitStatus() {
        return 0;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void close() {

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
