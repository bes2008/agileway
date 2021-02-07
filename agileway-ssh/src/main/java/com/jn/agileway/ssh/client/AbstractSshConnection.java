package com.jn.agileway.ssh.client;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.AnyHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.IOs;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractSshConnection<CONF extends SshConnectionConfig> implements SshConnection<CONF> {
    private String connId;
    protected CONF sshConfig;
    private SshConnectionStatus status = SshConnectionStatus.INITIALING;
    protected AnyHostKeyVerifier hostKeyVerifier = new AnyHostKeyVerifier();

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
    public void addHostKeyVerifier(HostKeyVerifier hostKeyVerifier) {
        this.hostKeyVerifier.add(hostKeyVerifier);
    }

    @Override
    public boolean authenticateWithPublicKey(String user, File pemFile, String passphrase) throws SshException {
        if (pemFile == null) {
            throw new IllegalArgumentException("pemFile argument is null");
        }
        char[] buff = new char[256];

        CharArrayWriter cw = new CharArrayWriter();
        FileReader fr = null;
        try {
            fr = new FileReader(pemFile);
            int len = 0;
            while ((len = fr.read(buff)) != -1) {
                cw.write(buff, 0, len);
            }
        } catch (IOException ex) {
            throw new SshException(ex.getMessage(), ex);
        } finally {
            IOs.close(fr);
        }


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

    @Override
    public final void close() throws IOException {
        this.hostKeyVerifier.clear();
        setStatus(SshConnectionStatus.CLOSED);
        doClose();
    }

    protected abstract void doClose() throws IOException;
}
