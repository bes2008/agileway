package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.langx.util.io.IOs;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SshjForwardingClient implements ForwardingClient {
    private SshjConnection connection;

    SshjForwardingClient(SshjConnection connection) {
        this.connection = connection;
    }

    private Map<String, LocalForwardingChannelCtx> localForwardingMap = new HashMap<String, LocalForwardingChannelCtx>();

    static class LocalForwardingChannelCtx implements Closeable {
        ForwardingChannel channel;
        LocalPortForwarder forwarder;
        Thread thread;

        @Override
        public void close() throws IOException {
            thread.interrupt();
        }
    }

    @Override
    public ForwardingChannel startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SSHClient delegate = connection.getDelegate();
        ForwardingChannel channel = new ForwardingChannel(ForwardingChannel.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
        if (!localForwardingMap.containsKey(channel.toString())) {
            SocketAddress address = new InetSocketAddress(bindToHost, bindToPort);
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(address);
                LocalPortForwarder.Parameters parameters = new LocalPortForwarder.Parameters(bindToHost, bindToPort, destHost, destPort);
                final LocalPortForwarder forwarder = delegate.newLocalPortForwarder(parameters, serverSocket);
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            forwarder.listen();
                        } catch (Throwable ex) {
                            // ignore it , interrupted
                        }
                    }
                };
                t.start();

                LocalForwardingChannelCtx ctx = new LocalForwardingChannelCtx();
                ctx.thread = t;
                ctx.channel = channel;
                ctx.forwarder = forwarder;
                localForwardingMap.put(channel.toString(), ctx);
            } catch (Throwable ex) {
                throw new SshException(ex);
            }
        }
        return channel;
    }

    @Override
    public void stopLocalForwarding(ForwardingChannel channel) throws SshException {
        if (localForwardingMap.containsKey(channel.toString())) {
            LocalForwardingChannelCtx ctx = localForwardingMap.remove(channel.toString());
            IOs.close(ctx);
        }
    }

    @Override
    public ForwardingChannel startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SSHClient delegate = connection.getDelegate();
        RemotePortForwarder forwarder = delegate.getRemotePortForwarder();
        RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(bindToHost, bindToPort);
        try {
            forwarder.bind(forward, new SocketForwardingConnectListener(new InetSocketAddress(bindToHost, bindToPort)));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannel(ForwardingChannel.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannel channel) throws SshException {
        SSHClient delegate = connection.getDelegate();
        RemotePortForwarder forwarder = delegate.getRemotePortForwarder();
        RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(channel.getBindingHost(), channel.getBindingPort());
        try {
            forwarder.cancel(forward);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }
}
