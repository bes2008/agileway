package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;

public class JschForwardingClient implements ForwardingClient {

    private JschConnection connection;

    JschForwardingClient(JschConnection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannel startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.setPortForwardingL(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannel(ForwardingChannel.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopLocalForwarding(ForwardingChannel channel) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.delPortForwardingL(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingChannel startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.setPortForwardingR(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannel(ForwardingChannel.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannel channel) throws SshException {
        Session session = this.connection.delegate();
        try {
            session.delPortForwardingR(channel.getBindingHost(), channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
