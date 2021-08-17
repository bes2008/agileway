package com.jn.agileway.ssh.client.impl.trileadssh2;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.langx.util.io.IOs;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.LocalPortForwarder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Ssh2ForwardingClient implements ForwardingClient {
    private Ssh2Connection connection;
    private Map<String, LocalPortForwarder> localForwarderMap = new ConcurrentHashMap<String, LocalPortForwarder>();

    Ssh2ForwardingClient(Ssh2Connection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannel startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannel channel = new ForwardingChannel(ForwardingChannel.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
        if (!localForwarderMap.containsKey(channel.toString())) {
            Connection delegate = this.connection.getDelegate();
            try {
                LocalPortForwarder localPortForwarder = delegate.createLocalPortForwarder(new InetSocketAddress(bindToHost, bindToPort), destHost, destPort);
                localForwarderMap.put(channel.toString(), localPortForwarder);
            } catch (Throwable ex) {
                throw new SshException(ex);
            }
        }
        return channel;
    }

    @Override
    public void stopLocalForwarding(ForwardingChannel channel) throws SshException {
        LocalPortForwarder forwarder = localForwarderMap.remove(channel.toString());
        if (forwarder != null) {
            IOs.close(forwarder);
        }
    }

    @Override
    public ForwardingChannel startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        Connection delegate = this.connection.getDelegate();
        ForwardingChannel channel = new ForwardingChannel(ForwardingChannel.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
        try {
            delegate.requestRemotePortForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return channel;
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannel channel) throws SshException {
        Connection delegate = this.connection.getDelegate();
        try {
            delegate.cancelRemotePortForwarding(channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
