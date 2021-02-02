package com.jn.agileway.ssh.client.impl.jsch;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum ChannelType implements CommonEnum {
    SESSION(0, "session", "will open a session"),
    SHELL(1, "shell", "shell"),
    /**
     * 该方式只能用来一次性执行命令
     */
    EXEC(2, "exec", "execute a command"),
    X11(3, "x11", "x11"),
    AGENT_FORWARDING(4, "auth-agent@openssh.com", "agent"),
    DIRECT_TCP_IP(5, "direct-tcpip", "direct"),
    FORWARDED_TCP_IP(6, "forwarded-tcpip", "forwarded"),
    SFTP(7, "sftp", "sftp"),
    SUBSYSTEM(8, "subsystem", "subsystem");

    private final EnumDelegate delegate;

    ChannelType(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }
}
