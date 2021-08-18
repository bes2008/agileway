package com.jn.agileway.ssh.client.channel.forwarding;

import com.jn.agileway.ssh.client.SshException;

public interface ForwardingClient {
    /**
     * 建立 local forwarding 隧道
     *
     * 隧道作用：
     *      1) 建立隧道： 当前机器 host:port ---> ssh_server ---> destination host:port
     *      2）之后发给 当前机器 host:port 的请求，都会通过 该隧道 转发给 destination host:port
     * 应用场景：
     *      当前机器可以通过ssh_server 来访问 destination
     *
     * @param bindToHost    the local host will binding
     * @param bindToPort    the local port will binding
     * @param destHost      the destination host you will to access
     * @param destPort      the destination port you will to access
     */
    ForwardingChannelInfo startLocalForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException;
    void stopLocalForwarding(ForwardingChannelInfo channel) throws SshException;
    /**
     * 建立remote forwarding 隧道
     *
     * 隧道作用：
     *      1）建立隧道：远程机器 host:port ---> ssh_server ---> destination host:port
     *      2）之后发给 远程机器 host:port 的请求，都会通过 该隧道 转发给 destination host:port
     * 应用场景：
     *      当前机器 不能使用ssh 连接 ssh_server，但ssh_server可以连接到远程机器的场景
     *
     * @param bindToHost    the remote host will binding
     * @param bindToPort    the remote port will binding
     * @param destHost      the destination host you will to access
     * @param destPort      the destination port you will to access
     */
    ForwardingChannelInfo startRemoteForwarding(String bindToHost, int bindToPort, String destHost, int destPort) throws SshException;
    void stopRemoteForwarding(ForwardingChannelInfo channel) throws SshException;

}
