package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.AbstarctSessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.IOs;
import net.schmizz.sshj.connection.channel.direct.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class SshjSessionedChannel extends AbstarctSessionedChannel {

    private Session session;

    private Session.Command command;
    private Session.Subsystem subsystem;
    private Session.Shell shell;

    private InputStream inputStream;
    private InputStream errorInputStream;
    private OutputStream outputStream;


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
    public void env(String variableName, String variableValue) throws SshException {
        try {
            this.session.setEnvVar(variableName, variableValue);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalX11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            this.session.reqX11Forwarding(x11AuthenticationProtocol, x11AuthenticationCookie, x11ScreenNumber);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalExec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        try {
            this.command = this.session.exec(command);
            initStreams();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalSubsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        try {
            this.subsystem = this.session.startSubsystem(subsystem);
            initStreams();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalShell() throws SshException {
        try {
            this.shell = this.session.startShell();
            initStreams();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    private void initStreams() {
        getInputStream();
        getErrorInputStream();
        getOutputStream();
    }

    private void closeStreams() {
        IOs.close(this.outputStream);
        this.outputStream = null;
        IOs.close(this.inputStream);
        this.inputStream = null;
        IOs.close(this.errorInputStream);
        this.errorInputStream = null;
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
    public void close() throws IOException {
        closeStreams();
        this.session.close();
    }


    @Override
    public int getExitStatus() {
        if (subsystem != null) {
            long maxWait = 30000; // 5s
            Integer exitStatus = subsystem.getExitStatus();
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

                exitStatus = subsystem.getExitStatus();
            }
            return exitStatus == null ? 0 : exitStatus;
        }
        if (command != null) {
            long maxWait = 30000;
            Integer exitStatus = command.getExitStatus();
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

                exitStatus = command.getExitStatus();
            }
            return exitStatus == null ? 0 : exitStatus;
        }
        return 0;
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        if (this.errorInputStream == null) {
            if (shell != null) {
                this.errorInputStream = shell.getErrorStream();
            } else if (command != null) {
                this.errorInputStream = command.getErrorStream();
            }
        }
        return this.errorInputStream;
    }

    @Override
    public InputStream getInputStream() throws SshException {
        if (this.inputStream == null) {
            if (shell != null) {
                this.inputStream = shell.getInputStream();
            }
            if (subsystem != null) {
                this.inputStream = subsystem.getInputStream();
            }
            if (command != null) {
                this.inputStream = command.getInputStream();
            }
        }
        return this.inputStream;
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        if (this.outputStream == null) {
            if (shell != null) {
                this.outputStream = shell.getOutputStream();
            }
            if (subsystem != null) {
                this.outputStream = subsystem.getOutputStream();
            }
            if (command != null) {
                this.outputStream = command.getOutputStream();
            }
        }
        return this.outputStream;
    }

    protected void beforeAction() {
        closeStreams();
        this.shell = null;
        this.command = null;
        this.subsystem = null;
    }
}
