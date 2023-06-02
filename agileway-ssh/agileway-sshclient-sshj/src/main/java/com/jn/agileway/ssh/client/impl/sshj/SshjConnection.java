package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.agileway.ssh.client.impl.sshj.sftp.SshjSftpSession;
import com.jn.agileway.ssh.client.impl.sshj.verifier.ToSshjHostKeyVerifierAdapter;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.Nets;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;
import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.PasswordUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class SshjConnection extends AbstractSshConnection<SshjConnectionConfig> {
    private Logger logger = Loggers.getLogger(SshjConnection.class);
    private SSHClient sshClient;

    private void makeSureSshClient() {
        if (sshClient == null) {
            sshClient = new SSHClient();
        }
    }

    public SSHClient getSshClient() {
        return this.sshClient;
    }

    @Override
    public void connect(String host, int port) throws SshException {
        try {
            connect(InetAddress.getByName(host), port);
        } catch (UnknownHostException ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void connect(InetAddress host, int port) throws SshException {
        connect(host, port, null, -1);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SshException {
        try {
            makeSureSshClient();
            if (!sshClient.isConnected()) {
                if (!this.hostKeyVerifier.isEmpty()) {
                    sshClient.addHostKeyVerifier(new ToSshjHostKeyVerifierAdapter(this.hostKeyVerifier));
                }
                if (localAddr == null || !Nets.isValidPort(localPort)) {
                    sshClient.connect(host, port);
                } else {
                    sshClient.connect(host, port, localAddr, localPort);
                }
            }
            setStatus(SshConnectionStatus.CONNECTED);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        Preconditions.checkNotNull(sshClient);
        try {
            sshClient.authPassword(user, password);
            return true;
        } catch (UserAuthException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPublicKey(String user, byte[] pemPrivateKey, String passphrase) throws SshException {
        Preconditions.checkNotNull(sshClient);

        String keyContent = new String(pemPrivateKey);

        List<KeyProvider> keyProviders = Collects.emptyArrayList();

        PasswordFinder passwordFinder = null;
        if (Strings.isNotBlank(passphrase)) {
            passwordFinder = PasswordUtils.createOneOff(passphrase.toCharArray());
        }
        try {
            StringReader reader = new StringReader(keyContent);
            PuTTYKeyFile puTTYKeyFile = new PuTTYKeyFile();
            if (passwordFinder == null) {
                puTTYKeyFile.init(reader);
            } else {
                puTTYKeyFile.init(reader, passwordFinder);
            }
            puTTYKeyFile.getPrivate();
            keyProviders.add(puTTYKeyFile);
        } catch (Throwable ex) {
            // ignore it
        }

        try {
            StringReader reader = new StringReader(keyContent);
            PKCS8KeyFile pkcs8KeyFile = new PKCS8KeyFile();
            if (passwordFinder == null) {
                pkcs8KeyFile.init(reader);
            } else {
                pkcs8KeyFile.init(reader, passwordFinder);
            }
            pkcs8KeyFile.getPrivate();
            keyProviders.add(pkcs8KeyFile);
        } catch (Throwable ex) {
            // ignore it
        }

        try {
            StringReader reader = new StringReader(keyContent);
            OpenSSHKeyFile openSSHKeyFile = new OpenSSHKeyFile();
            if (passwordFinder == null) {
                openSSHKeyFile.init(reader);
            } else {
                openSSHKeyFile.init(reader, passwordFinder);
            }
            openSSHKeyFile.getPrivate();
            keyProviders.add(openSSHKeyFile);
        } catch (Throwable ex) {
            // ignore it
        }

        try {
            Preconditions.checkState(!keyProviders.isEmpty(), "the private key is invalid: " + keyContent);
            sshClient.authPublickey(user, keyProviders);
        } catch (UserAuthException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
        return false;
    }


    @Override
    public SessionedChannel openSession() throws SshException {
        Preconditions.checkState(getStatus() == SshConnectionStatus.CONNECTED, "ssh not connected");
        try {
            Session session = sshClient.startSession();
            return new SshjSessionedChannel(session);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingClient forwardingClient() {
        return null;
    }

    @Override
    protected void doClose() throws IOException {
        this.sshClient.close();
    }

    @Override
    public SftpSession openSftpSession() throws SshException {
        try {
            SFTPClient client = sshClient.newSFTPClient();
            SshjSftpSession session = new SshjSftpSession(client);
            session.setSshConnection(this);
            return session;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    SSHClient getDelegate() {
        return sshClient;
    }
}
