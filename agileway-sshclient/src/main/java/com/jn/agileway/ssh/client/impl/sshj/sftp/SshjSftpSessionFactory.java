package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnection;
import com.jn.agileway.ssh.client.sftp.SftpSessionFactory;
import com.jn.langx.util.Preconditions;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshjSftpSessionFactory implements SftpSessionFactory<SshjSftpSession> {
    private static final Logger logger = LoggerFactory.getLogger(SshjSftpSessionFactory.class);

    @Override
    public SshjSftpSession get(SshConnection sshConnection) {
        Preconditions.checkArgument(sshConnection instanceof SshjConnection);
        SshjConnection conn = (SshjConnection) sshConnection;
        SSHClient sshClient = conn.getSshClient();
        try {
            SFTPClient client = sshClient.newSFTPClient();
            return new SshjSftpSession(client);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
