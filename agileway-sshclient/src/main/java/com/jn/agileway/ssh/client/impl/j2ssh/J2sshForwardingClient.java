package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.sshtools.j2ssh.SshClient;

public class J2sshForwardingClient implements ForwardingClient {
    private J2sshConnection connection;

    J2sshForwardingClient(J2sshConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannel startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannel channel = new ForwardingChannel(ForwardingChannel.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);

        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();

        try {
            if (!delegate.getLocalForwardings().containsKey(channel.toString())) {
                delegate.addLocalForwarding(channel.toString(), bindToHost, bindToPort, destHost, destPort);
            }
            delegate.startLocalForwarding(channel.toString());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return channel;
    }

    @Override
    public void stopLocalForwarding(ForwardingChannel channel) throws SshException {
        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();
        try {
            delegate.stopLocalForwarding(channel.toString());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannel startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannel channel = new ForwardingChannel(ForwardingChannel.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);

        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();

        try {
            if (!delegate.getRemoteForwardings().containsKey(channel.toString())) {
                delegate.addRemoteForwarding(channel.toString(), bindToHost, bindToPort, destHost, destPort);
            }
            delegate.startRemoteForwarding(channel.toString());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }

        return channel;
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannel channel) throws SshException {
        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();
        try {
            delegate.stopRemoteForwarding(channel.toString());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
