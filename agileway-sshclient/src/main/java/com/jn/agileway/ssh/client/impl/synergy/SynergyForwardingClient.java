package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.sshtools.client.SshClient;

public class SynergyForwardingClient implements ForwardingClient {
    private SynergyConnection connection;

    SynergyForwardingClient(SynergyConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannel startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.startLocalForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannel(ForwardingChannel.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopLocalForwarding(ForwardingChannel channel) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.stopLocalForwarding(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannel startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.startRemoteForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannel(ForwardingChannel.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }


    @Override
    public void stopRemoteForwarding(ForwardingChannel channel) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.stopRemoteForwarding(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
