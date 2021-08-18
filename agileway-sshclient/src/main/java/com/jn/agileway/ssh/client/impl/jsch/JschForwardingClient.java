package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannelInfo;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;

public class JschForwardingClient implements ForwardingClient {

    private JschConnection connection;

    JschForwardingClient(JschConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.setPortForwardingL(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.delPortForwardingL(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.setPortForwardingR(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.delPortForwardingR(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
