package com.jn.agileway.ssh.client.channel;

import com.jn.agileway.ssh.client.SshException;

public abstract class AbstarctSessionedChannel implements SessionedChannel {
    private String channelType = TYPE_DEFAULT;

    @Override
    public final void x11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException {
        try {
            beforeAction();
            internalX11Forwarding(hostname, port, singleConnection, x11AuthenticationProtocol, x11AuthenticationCookie, x11ScreenNumber);
        } finally {
            this.channelType = TYPE_X11;
        }
    }

    protected abstract void internalX11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException;

    @Override
    public final void exec(String command) throws SshException {
        try {
            beforeAction();
            internalExec(command);
        } finally {
            this.channelType = TYPE_EXEC;
        }
    }

    protected abstract void internalExec(String command) throws SshException;

    @Override
    public final void subsystem(String subsystem) throws SshException {
        try {
            beforeAction();
            internalSubsystem(subsystem);
        } finally {
            this.channelType = TYPE_EXEC;
        }
    }

    protected abstract void internalSubsystem(String subsystem) throws SshException;

    @Override
    public final void shell() throws SshException {
        try {
            beforeAction();
            internalShell();
        } finally {
            this.channelType = TYPE_SHELL;
        }
    }

    protected abstract void internalShell() throws SshException;


    protected void beforeAction() {
    }

    @Override
    public String getType() {
        return channelType;
    }
}
