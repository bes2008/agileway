package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.function.Consumer2;
import net.schmizz.sshj.connection.channel.direct.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class SshjSessionedChannel implements SessionedChannel {

    private Session session;

    private Session.Command command;
    private Session.Subsystem subsystem;
    private Session.Shell shell;

    SshjSessionedChannel(@NonNull Session session) {
        Preconditions.checkNotNull(session);
        this.session = session;
    }

    @Override
    public void pty(String term) throws SshException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException {
        final Map<net.schmizz.sshj.connection.channel.direct.PTYMode, Integer> terminalModeMap = new HashMap<net.schmizz.sshj.connection.channel.direct.PTYMode, Integer>();
        Collects.forEach(terminalModes, new Consumer2<PTYMode, Integer>() {
            @Override
            public void accept(PTYMode ptyMode, Integer value) {
                String operateName = ptyMode.name();
                net.schmizz.sshj.connection.channel.direct.PTYMode mode = Enums.ofName(net.schmizz.sshj.connection.channel.direct.PTYMode.class, operateName);
                if (mode != null) {
                    terminalModeMap.put(mode, value);
                }
            }
        });
        try {
            this.session.allocatePTY(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels, terminalModeMap);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void x11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            this.session.reqX11Forwarding(x11AuthenticationProtocol, x11AuthenticationCookie, x11ScreenNumber);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void env(String variableName, String variableValue) throws SshException {
        try {
            this.session.setEnvVar(variableName, variableValue);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void exec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        try {
            this.command = this.session.exec(command);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void subsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        try {
            this.subsystem = this.session.startSubsystem(subsystem);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void shell() throws SshException {
        try {
            this.shell = this.session.startShell();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void signal(Signal signal) throws SshException {
        Preconditions.checkNotEmpty(signal, "the signal is null or empty");
        Preconditions.checkArgument(signal != Signal.UNKNOWN, "the signal is UNKNOWN");
        net.schmizz.sshj.connection.channel.direct.Signal sg = null;
        String name = signal.name();
        sg = net.schmizz.sshj.connection.channel.direct.Signal.fromString(name);
        try {
            if (this.shell != null) {
                this.shell.signal(sg);
            }
            if (this.command != null) {
                this.command.signal(sg);
            }
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public String getType() {
        if (shell != null) {
            return "shell";
        }
        if (subsystem != null) {
            return "subsystem";
        }
        if (command != null) {
            return "exec";
        }
        return "session";
    }

    @Override
    public void close() throws IOException {
        this.session.close();
    }

    @Override
    public int getExitStatus() {
        if (subsystem != null) {
            return subsystem.getExitStatus();
        }
        if (command != null) {
            return command.getExitStatus();
        }
        return 0;
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        if (shell != null) {
            return shell.getErrorStream();
        }
        if (command != null) {
            return command.getErrorStream();
        }
        return null;
    }

    @Override
    public InputStream getInputStream() throws SshException {
        if (shell != null) {
            return shell.getInputStream();
        }
        if (subsystem != null) {
            return subsystem.getInputStream();
        }
        if (command != null) {
            return command.getInputStream();
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        if (shell != null) {
            return shell.getOutputStream();
        }
        if (subsystem != null) {
            return subsystem.getOutputStream();
        }
        if (command != null) {
            return command.getOutputStream();
        }
        return null;
    }
}
