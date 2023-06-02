package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannelInfo;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.sshtools.j2ssh.SshClient;

public class J2sshForwardingClient implements ForwardingClient {
    private J2sshConnection connection;

    J2sshForwardingClient(J2sshConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannelInfo channel = new ForwardingChannelInfo(ForwardingChannelInfo.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);

        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();

        try {
            if (!delegate.getLocalForwardings().containsKey(ForwardingChannelInfo.id(channel))) {
                delegate.addLocalForwarding(ForwardingChannelInfo.id(channel), bindToHost, bindToPort, destHost, destPort);
            }
            delegate.startLocalForwarding(ForwardingChannelInfo.id(channel));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return channel;
    }

    @Override
    public void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException {
        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();
        try {
            delegate.stopLocalForwarding(ForwardingChannelInfo.id(channel));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannelInfo channel = new ForwardingChannelInfo(ForwardingChannelInfo.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);

        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();

        try {
            if (!delegate.getRemoteForwardings().containsKey(ForwardingChannelInfo.id(channel))) {
                delegate.addRemoteForwarding(ForwardingChannelInfo.id(channel), bindToHost, bindToPort, destHost, destPort);
            }
            delegate.startRemoteForwarding(ForwardingChannelInfo.id(channel));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }

        return channel;
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException {
        SshClient sshClient = this.connection.getDelegate();
        com.sshtools.j2ssh.forwarding.ForwardingClient delegate = sshClient.getForwardingClient();
        try {
            delegate.stopRemoteForwarding(ForwardingChannelInfo.id(channel));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
