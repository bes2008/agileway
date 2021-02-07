package com.jn.agileway.ssh.client;

import com.jn.langx.util.Preconditions;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractSshConnection<CONF extends SshConnectionConfig> implements SshConnection<CONF> {
    private String connId;
    protected CONF sshConfig;
    protected SshConnectionStatus status = SshConnectionStatus.INITIALING;

    @Override
    public String getId() {
        return connId;
    }

    @Override
    public void setId(String id) {
        this.connId = id;
    }

    @Override
    public String getHost() {
        return getConfig().getHost();
    }

    public void setConfig(CONF connConfig) {
        this.sshConfig = connConfig;
    }

    @Override
    public CONF getConfig() {
        return sshConfig;
    }

    @Override
    public int getPort() {
        return getConfig().getPort();
    }

    @Override
    public boolean authenticateWithPublicKey(String user, File pemFile, String passphrase) throws IOException {
        if (pemFile == null) {
            throw new IllegalArgumentException("pemFile argument is null");
        }
        char[] buff = new char[256];

        CharArrayWriter cw = new CharArrayWriter();
        FileReader fr = new FileReader(pemFile);
        int len = 0;
        while ((len = fr.read(buff)) != -1) {
            cw.write(buff, 0, len);
        }
        fr.close();

        return authenticateWithPublicKey(user, cw.toCharArray(), passphrase);
    }

    @Override
    public final boolean isClosed() {
        return status == SshConnectionStatus.CLOSED;
    }

    @Override
    public final boolean isConnected() {
        return status.getCode() >= SshConnectionStatus.CONNECTED.getCode();
    }

    @Override
    public SshConnectionStatus getStatus() {
        return status;
    }

    protected void setStatus(SshConnectionStatus status) {
        Preconditions.checkNotNull(status);
        this.status = status;
    }
}
