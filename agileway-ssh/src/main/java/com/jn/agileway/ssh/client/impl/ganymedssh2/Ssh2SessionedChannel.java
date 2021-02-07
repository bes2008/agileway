package com.jn.agileway.ssh.client.impl.ganymedssh2;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Ssh2SessionedChannel implements SessionedChannel {
    @Override
    public InputStream getErrorInputStream() throws IOException {
        return null;
    }

    @Override
    public void pty(String term) {

    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, byte[] terminalModes) {

    }

    @Override
    public void x11Forwarding(boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) {

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
    public InputStream getStdInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream getStdOutputStream() throws IOException {
        return null;
    }
}
