package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.*;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

class JschSessionedChannel implements SessionedChannel {
    private Session session;

    private Channel channel;
    private JschChannelType channelType;

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
    private byte[] terminalModes = null;

    JschSessionedChannel(Session session) {
        Preconditions.checkNotNull(session);
        this.session = session;
    }


    @Override
    public void pty(String term) throws IOException {
        pty(term, 0, 0, 0, 0, null);
    }

    @Override
    public void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, byte[] terminalModes) throws IOException {
        this.term = term;
        this.termWidthCharacters = termWidthCharacters;
        this.termHeightCharacters = termHeightCharacters;
        this.termWidthPixels = termWidthPixels;
        this.termHeightPixels = termHeightPixels;
        this.terminalModes = terminalModes;
    }

    @Override
    public void x11Forwarding(String host, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws IOException {
        this.x11Forwarding = true;
    }

    @Override
    public void exec(String command) throws SshException {
        Preconditions.checkNotEmpty(command, "the command is illegal : {}", command);
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        this.channelType = JschChannelType.EXEC;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel(channelType.getName());
            channel.setCommand(command);
            this.channel = channel;
            startChannel();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void subsystem(String subsystem) throws SshException {
        Preconditions.checkNotEmpty(subsystem, "the subsystem is illegal : {}", subsystem);
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        try {
            this.channelType = JschChannelType.SUBSYSTEM;
            ChannelSubsystem channel = (ChannelSubsystem) session.openChannel(channelType.getName());
            channel.setSubsystem(subsystem);
            this.channel = channel;
            startChannel();
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void shell() throws SshException {
        Preconditions.checkState(session != null && session.isConnected(), "the session is not connected");
        try {
            this.channelType = JschChannelType.SHELL;
            this.channel = session.openChannel(channelType.getName());
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
            switch (channelType) {
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

        // pty:
        switch (channelType) {
            case SUBSYSTEM:
                ChannelSubsystem subsystem = (ChannelSubsystem) channel;
                subsystem.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                subsystem.setTerminalMode(terminalModes);
                break;
            case EXEC:
                ChannelExec exec = (ChannelExec) channel;
                exec.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                exec.setTerminalMode(terminalModes);
                break;
            case SHELL:
                ChannelShell shell = (ChannelShell) channel;
                shell.setPtyType(term, termWidthCharacters, termHeightCharacters, termWidthPixels, termHeightPixels);
                shell.setTerminalMode(terminalModes);
                break;
            default:
                break;
        }

        // env:
        if (!this.envVariables.isEmpty()) {
            switch (channelType) {
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
    public void env(String variableName, String variableValue) {
        this.envVariables.put(variableName, variableValue);
    }

    @Override
    public void signal(String signal) throws SshException {
        Preconditions.checkState(channel != null && channel.isConnected());
        Preconditions.checkNotEmpty(signal);
        try {
            channel.sendSignal(signal);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public int getExitStatus() {
        return channel.getExitStatus();
    }

    @Override
    public String getType() {
        return "session";
    }

    @Override
    public void close() {
        session = null;
        if (this.channel != null) {
            this.channel.disconnect();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.channel.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.channel.getOutputStream();
    }

    @Override
    public InputStream getErrorInputStream() throws IOException {
        Preconditions.checkState(channel != null);
        InputStream errorInputStream = null;
        switch (channelType) {
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
    }

}
