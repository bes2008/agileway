package com.jn.agileway.ssh.client.channel;

import com.jn.langx.util.struct.Holder;

public class DefaultShellExecutor implements ShellExecutor{
    private SessionedChannel channel;
    public DefaultShellExecutor(SessionedChannel channel){
        this.channel=channel;
    }

    @Override
    public SessionedChannel getChannel() {
        return channel;
    }

    @Override
    public boolean execute(String statementBlock, String moreFlagLine, String more, long responseTime, int maxAttempts, Holder<String> stdout, Holder<String> stderr) {
        return false;
    }
}
