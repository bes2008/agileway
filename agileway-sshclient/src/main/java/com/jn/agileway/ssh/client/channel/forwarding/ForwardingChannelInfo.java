package com.jn.agileway.ssh.client.channel.forwarding;

import com.jn.langx.text.StringTemplates;

public class ForwardingChannelInfo {


    private static final String REMOTE_FORWARD_REQUEST = "tcpip-forward";
    private static final String REMOTE_FORWARD_CANCEL_REQUEST = "cancel-tcpip-forward";

    private String bindingHost = "localhost";
    private int bindingPort;
    private String destHost = "localhost";
    private int destPort;


    public static final String X11_FORWARDING_CHANNEL = "x11";
    public static final String LOCAL_FORWARDING_CHANNEL = "direct-tcpip";
    public static final String REMOTE_FORWARDING_CHANNEL = "forwarded-tcpip";

    /**
     * @see #LOCAL_FORWARDING_CHANNEL
     * @see #REMOTE_FORWARDING_CHANNEL
     */
    private String channel;

    public ForwardingChannelInfo() {
    }

    public ForwardingChannelInfo(String channel, String bindingHost, int bindingPort, String destHost, int destPort) {
        setBindingHost(bindingHost);
        setBindingPort(bindingPort);
        setDestHost(destHost);
        setDestPort(destPort);
    }

    public String getBindingHost() {
        return bindingHost;
    }

    public void setBindingHost(String bindingHost) {
        this.bindingHost = bindingHost;
    }

    public int getBindingPort() {
        return bindingPort;
    }

    public void setBindingPort(int bindingPort) {
        this.bindingPort = bindingPort;
    }

    public String getDestHost() {
        return destHost;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("{}({}:{}->{}:{})", channel, bindingHost, bindingPort, destHost, destPort);
    }
}
