package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

public class CommentHostsKeyEntry extends AbstractHostsKeyEntry {
    private final String comment;

    public CommentHostsKeyEntry(String comment) {
        this.comment = comment;
    }

    @Override
    public String getLine() {
        return comment;
    }

    @Override
    protected boolean containsHost(String host) {
        return false;
    }
}
