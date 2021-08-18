package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.LocalPortForwarder;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannelInfo;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.langx.util.io.IOs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Ssh2ForwardingClient implements ForwardingClient {
    private Ssh2Connection connection;

    private Map<String, LocalPortForwarder> localForwarderMap = new ConcurrentHashMap<String, LocalPortForwarder>();

    Ssh2ForwardingClient(Ssh2Connection connection) {
        this.connection = connection;
    }

    @Override
    public ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ForwardingChannelInfo channel = new ForwardingChannelInfo(ForwardingChannelInfo.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
        LocalPortForwarder forwarder = localForwarderMap.get(channel);
        if (forwarder == null) {
            try {
                ch.ethz.ssh2.Connection delegate = this.connection.getDelegate();
                forwarder = delegate.createLocalPortForwarder(bindToPort, destHost, destPort);
                localForwarderMap.put(ForwardingChannelInfo.id(channel), forwarder);
            } catch (Throwable ex) {
                throw new SshException(ex);
            }
        }
        return channel;
    }

    @Override
    public void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException {
        LocalPortForwarder forwarder = localForwarderMap.remove(ForwardingChannelInfo.id(channel));
        if (forwarder != null) {
            IOs.close(forwarder);
        }
    }

    @Override
    public ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        ch.ethz.ssh2.Connection delegate = this.connection.getDelegate();
        try {
            delegate.requestRemotePortForwarding(bindToHost, bindToPort, destHost, destPort);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException {
        ch.ethz.ssh2.Connection delegate = this.connection.getDelegate();
        try {
            delegate.cancelRemotePortForwarding(channel.getBindingPort());
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
