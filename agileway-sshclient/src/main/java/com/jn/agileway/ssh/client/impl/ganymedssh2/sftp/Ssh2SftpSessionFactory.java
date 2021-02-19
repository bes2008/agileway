package com.jn.agileway.ssh.client.impl.ganymedssh2.sftp;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2Connection;
import com.jn.agileway.ssh.client.sftp.SftpSessionFactory;

public class Ssh2SftpSessionFactory implements SftpSessionFactory<Ssh2SftpSession> {
    @Override
    public Ssh2SftpSession get(SshConnection sshConnection) {
        Ssh2Connection conn = (Ssh2Connection) sshConnection;
        Connection connection = conn.getDelegate();
        try {
            SFTPv3Client sftpClient = new SFTPv3Client(connection);
            Ssh2SftpSession session = new Ssh2SftpSession(sftpClient);
            return session;
        } catch (Throwable ex) {
            throw new SshException(ex.getMessage(), ex);
        }
    }
}
