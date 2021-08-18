package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.*;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.AbstarctSessionedChannel;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

class JschSessionedChannel extends AbstarctSessionedChannel {
    private Session session;

    private Channel channel;
    private JschChannelType type = JschChannelType.SESSION;

    private boolean agentForwarding = false;
    /**
     * X11 properties
     */
    private boolean x11Forwarding = false;

    /**
     * env
     */
    private Hashtable<String, String> envVariables = Collects.<String, String>emptyHashtable();

    /**
     * Pty properties:
     */
    private String term = "vt100";
    private int termWidthCharacters = 80;
    private int termHeightCharacters = 24;
    private int termWidthPixels = 640;
    private int termHeightPixels = 480;
    private Map<PTYMode, Integer> terminalModes = null;

    JschSessionedChannel(Session session) {
        Preconditions.checkNotNull(session);
        this.session = session;
    }


    @Override
    public void pty(String term) throws SshException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException {
        this.term = term;
        this.termWidthCharacters = termWidthCharacters;
        this.termHeightCharacters = termHeightCharacters;
        this.termWidthPixels = termWidthPixels;
        this.termHeightPixels = termHeightPixels;
        this.terminalModes = terminalModes;
    }

    @Override
    protected void internalX11Forwarding(String host, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        this.x11Forwarding = true;
        session.setX11Host(host);
        session.setX11Port(port);
        session.setX11Cookie(x11AuthenticationCookie);
    }

    @Override
    protected void internalExec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        this.type = JschChannelType.EXEC;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel(type.getName());
            channel.setCommand(command);
            this.channel = channel;
            startChannel();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalSubsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        try {
            this.type = JschChannelType.SUBSYSTEM;
            ChannelSubsystem channel = (ChannelSubsystem) session.openChannel(type.getName());
            channel.setSubsystem(subsystem);
            this.channel = channel;
            startChannel();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    protected void internalShell() throws SshException {
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        try {
            this.type = JschChannelType.SHELL;
            this.channel = session.openChannel(type.getName());
            startChannel();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    private void startChannel() throws JSchException {
        prepareConnect();
        channel.connect();
    }

    private void prepareConnect() {
        // agent forward:
        {
            switch (type) {
                case SUBSYSTEM:
                    ((ChannelSubsystem) channel).setAgentForwarding(agentForwarding);
                    break;
                case EXEC:
                    ((ChannelExec) channel).setAgentForwarding(agentForwarding);
                    break;
                case SHELL:
                    ((ChannelShell) channel).setAgentForwarding(agentForwarding);
                    break;
                default:
                    break;
            }
        }

        // x11 forward:
        channel.setXForwarding(x11Forwarding);


        byte[] terminalModesBytes = null;
        if (Emptys.isNotEmpty(this.terminalModes)) {
            terminalModesBytes = PTYMode.encode(terminalModes);
        }
        // pty:
        switch (type) {
            case SUBSYSTEM:
                ChannelSubsystem subsystem = (ChannelSubsystem) channel;
                subsystem.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                subsystem.setTerminalMode(terminalModesBytes);
                break;
            case EXEC:
                ChannelExec exec = (ChannelExec) channel;
                exec.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                exec.setTerminalMode(terminalModesBytes);
                break;
            case SHELL:
                ChannelShell shell = (ChannelShell) channel;
                shell.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                shell.setTerminalMode(terminalModesBytes);
                break;
            default:
                break;
        }

        // env:
        if (!this.envVariables.isEmpty()) {
            switch (type) {
                case SUBSYSTEM:
                    Collects.forEach(this.envVariables, new Consumer2<String, String>() {
                        @Override
                        public void accept(String variable, String value) {
                            ((ChannelSubsystem) channel).setEnv(variable, value);
                        }
                    });
                    break;
                case EXEC:
                    Collects.forEach(this.envVariables, new Consumer2<String, String>() {
                        @Override
                        public void accept(String variable, String value) {
                            ((ChannelExec) channel).setEnv(variable, value);
                        }
                    });
                    break;
                case SHELL:
                    Collects.forEach(this.envVariables, new Consumer2<String, String>() {
                        @Override
                        public void accept(String variable, String value) {
                            ((ChannelShell) channel).setEnv(variable, value);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void env(String variableName, String variableValue) throws SshException {
        this.envVariables.put(variableName, variableValue);
    }

    @Override
    public void signal(Signal signal) throws SshException {
        Preconditions.checkState(channel != null && channel.isConnected());
        Preconditions.checkNotEmpty(signal, "the signal is null or empty");
        Preconditions.checkArgument(signal != Signal.UNKNOWN, "the signal is UNKNOWN");
        try {
            channel.sendSignal(signal.name());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public int getExitStatus() {
        int exitStatus = channel.getExitStatus();
        long maxWait = 5000; // 5s
        while (exitStatus == -1) {
            try {
                int timeout = 10;
                maxWait = maxWait - timeout;
                wait(timeout);
            } catch (Throwable ex) {
                // ignore it
            }
            exitStatus = channel.getExitStatus();
        }
        return exitStatus;
    }

    @Override
    public String getType() {
        return this.type.getName();
    }

    @Override
    public void close() {
        session = null;
        if (this.channel != null) {
            this.channel.disconnect();
        }
    }

    @Override
    public InputStream getInputStream() throws SshException {
        try {
            return this.channel.getInputStream();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public OutputStream getOutputStream() throws SshException {
        try {
            return this.channel.getOutputStream();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public InputStream getErrorInputStream() throws SshException {
        Preconditions.checkState(channel != null);
        try {
            InputStream errorInputStream = null;
            switch (type) {
                case EXEC:
                    errorInputStream = ((ChannelExec) channel).getErrStream();
                    break;

                case SUBSYSTEM:
                    errorInputStream = ((ChannelSubsystem) channel).getErrStream();
                    break;
                case SHELL:
                    break;
                default:
                    break;
            }
            return errorInputStream;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

}
