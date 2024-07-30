package com.jn.agileway.ssh.client.channel;

import com.jn.agileway.ssh.client.SshException;

public abstract class AbstarctSessionedChannel implements SessionedChannel {
    private ChannelType channelType;

    @Override
    public final void x11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            beforeOpenX11();
            internalX11Forwarding(hostname, port, singleConnection, x11AuthenticationProtocol, x11AuthenticationCookie, x11ScreenNumber);
        } finally {
            this.channelType = ChannelType.X11;
        }
    }

    protected void beforeOpenX11() {
    }

    protected abstract void internalX11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException;

    @Override
    public final void exec(String command) throws SshException {
        try {
            beforeExec();
            internalExec(command);
        } finally {
            this.channelType = ChannelType.EXEC;
        }
    }
    protected void beforeExec() {
    }

    protected abstract void internalExec(String command) throws SshException;

    @Override
    public final void subsystem(String subsystem) throws SshException {
        try {
            beforeOpenSubsystem();
            internalSubsystem(subsystem);
        } finally {
            this.channelType = ChannelType.SUBSYSTEM;
        }
    }

    protected void beforeOpenSubsystem() {
    }

    protected abstract void internalSubsystem(String subsystem) throws SshException;

    @Override
    public final void shell() throws SshException {
        try {
            beforeOpenShell();
            internalShell();
        } finally {
            this.channelType = ChannelType.SHELL;
        }
    }

    protected void beforeOpenShell() {
    }

    protected abstract void internalShell() throws SshException;

    @Override
    public ChannelType getType() {
        return channelType;
    }
}
