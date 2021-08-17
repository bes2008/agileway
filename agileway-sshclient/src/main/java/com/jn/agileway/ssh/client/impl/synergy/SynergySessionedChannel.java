package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.sshtools.client.PseudoTerminalModes;
import com.sshtools.client.SessionChannelNG;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

class SynergySessionedChannel implements SessionedChannel {
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
    public void x11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {

    }



    @Override
    public void env(String variableName, String variableValue) throws SshException {

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
    public void signal(Signal signal) throws SshException {

    }

    @Override
    public int getExitStatus() {
        return 0;
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws SshException {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
