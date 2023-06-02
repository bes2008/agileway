package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingChannelInfo;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.reflect.Reflects;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SshjForwardingClient implements ForwardingClient {
    private static final Class LOCAL_PORT_FORWARDER_PARAMETERS;
    private static final Method NEW_LOCAL_PORT_FORWARDER;
    private SshjConnection connection;

    static {
        LOCAL_PORT_FORWARDER_PARAMETERS = findLocalPortForwardingParametersClass();
        NEW_LOCAL_PORT_FORWARDER=findNewLocalPortForwarder(LOCAL_PORT_FORWARDER_PARAMETERS);
    }

    SshjForwardingClient(SshjConnection connection) {
        this.connection = connection;
    }

    private Map<String, LocalForwardingChannelCtx> localForwardingMap = new HashMap<String, LocalForwardingChannelCtx>();

    static class LocalForwardingChannelCtx implements Closeable {
        ForwardingChannelInfo channel;
        LocalPortForwarder forwarder;
        Thread thread;

        @Override
        public void close() throws IOException {
            thread.interrupt();
        }
    }
    private static Class findLocalPortForwardingParametersClass(){
        ClassLoader expectedClassLoader= SSHClient.class.getClassLoader();
        String parametersClassInHierynomusSshjJar="net.schmizz.sshj.connection.channel.direct.Parameters";
        if(ClassLoaders.hasClass(parametersClassInHierynomusSshjJar, expectedClassLoader)) {
            try {
                return ClassLoaders.loadClass(parametersClassInHierynomusSshjJar, expectedClassLoader);
            }catch (ClassNotFoundException e){
                // ignore
            }
        }
        String parametersClassInNetSchmizzJar="net.schmizz.sshj.connection.channel.direct.LocalPortForwarder$Parameters";
        if(ClassLoaders.hasClass(parametersClassInNetSchmizzJar, expectedClassLoader)){
            try {
                return ClassLoaders.loadClass(parametersClassInNetSchmizzJar, expectedClassLoader);
            }catch (ClassNotFoundException e){
                // ignore
            }
        }
        throw new IllegalStateException("sshj is error version");
    }

    private static Method findNewLocalPortForwarder(Class parametersClass){
        return Reflects.getPublicMethod(SSHClient.class, "newLocalPortForwarder", new Class[]{parametersClass, ServerSocket.class});
    }

    @Override
    public ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SSHClient delegate = connection.getDelegate();
        ForwardingChannelInfo channel = new ForwardingChannelInfo(ForwardingChannelInfo.LOCAL_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
        if (!localForwardingMap.containsKey(ForwardingChannelInfo.id(channel))) {
            SocketAddress address = new InetSocketAddress(bindToHost, bindToPort);
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(address);
                Object parameters = Reflects.newInstance(LOCAL_PORT_FORWARDER_PARAMETERS, new Class[]{String.class, int.class, String.class, int.class}, bindToHost, bindToPort, destHost, destPort);

                final LocalPortForwarder forwarder = Reflects.invoke(NEW_LOCAL_PORT_FORWARDER, delegate, new Object[]{parameters, serverSocket}, true, true);
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
                localForwardingMap.put(ForwardingChannelInfo.id(channel), ctx);
            } catch (Throwable ex) {
                throw new SshException(ex);
            }
        }
        return channel;
    }




    @Override
    public void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException {
        if (localForwardingMap.containsKey(ForwardingChannelInfo.id(channel))) {
            LocalForwardingChannelCtx ctx = localForwardingMap.remove(ForwardingChannelInfo.id(channel));
            IOs.close(ctx);
        }
    }

    @Override
    public ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException {
        SSHClient delegate = connection.getDelegate();
        RemotePortForwarder forwarder = delegate.getRemotePortForwarder();
        RemotePortForwarder.Forward forward = new RemotePortForwarder.Forward(bindToHost, bindToPort);
        try {
            forwarder.bind(forward, new SocketForwardingConnectListener(new InetSocketAddress(bindToHost, bindToPort)));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return new ForwardingChannelInfo(ForwardingChannelInfo.REMOTE_FORWARDING_CHANNEL, bindToHost, bindToPort, destHost, destPort);
    }

    @Override
    public void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException {
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
