package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.SftpSessionFactory;
import com.jn.langx.util.io.IOs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpTests {
    private static final Logger logger = LoggerFactory.getLogger(SftpTests.class);

    @Test
    public void testSftp_sshj(){

    }

    void _test(SftpSessionFactory sessionFactory, SshConnectionFactory connectionFactory, SshConnectionConfig connectionConfig, String testWorkingDirectory) {
        SshConnection connection = connectionFactory.get(connectionConfig);
        SftpSession session = sessionFactory.get(connection);
        try {
            // 确保testWorkingDirectory 存在，并且是 empty的
            SftpFile workingDirectory = session.open(testWorkingDirectory, OpenMode.READ, null);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOs.close(session);
        }
    }
}
