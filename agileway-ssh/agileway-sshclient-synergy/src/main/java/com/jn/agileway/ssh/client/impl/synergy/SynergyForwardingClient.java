package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannelInfo;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.sshtools.client.SshClient;

public class SynergyForwardingClient implements ForwardingClient {
    private SynergyConnection connection;

    SynergyForwardingClient(SynergyConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.startLocalForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.stopLocalForwarding(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.startRemoteForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }


    @Override
    public void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException {
        SshClient sshClient = this.connection.getClient();
        try {
            sshClient.stopRemoteForwarding(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
