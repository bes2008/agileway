package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.sftp.SshjSftpSessionFactory;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.SftpSessionFactory;
import com.jn.agileway.ssh.client.sftp.Sftps;
import com.jn.langx.util.io.IOs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpTests {
    private static final Logger logger = LoggerFactory.getLogger(SftpTests.class);

    @Test
    public void testSftp_sshj() {
        _test(new SshjSftpSessionFactory(), new SshjConnectionFactory(), new SshjConnectionConfig(), "/home/fangjinuo/Templates/test_sftp_sshj");
    }

    void _test(SftpSessionFactory sessionFactory, SshConnectionFactory connectionFactory, AbstractSshConnectionConfig connectionConfig, String testWorkingDirectory) {
        connectionConfig.setHost("192.168.234.128");
        //connectionConfig.setHost("192.168.1.79");
        connectionConfig.setPort(22);
        connectionConfig.setUser("fangjinuo");
        connectionConfig.setPassword("fjn13570");

        SshConnection connection = connectionFactory.get(connectionConfig);

        SftpSession session = sessionFactory.get(connection);
        try {
            // 确保testWorkingDirectory 存在，并且是 empty的
            boolean testWorkingDirectoryExist = Sftps.existDirectory(session, testWorkingDirectory);
            logger.info("directory exist? {}", testWorkingDirectoryExist);
            if (!testWorkingDirectoryExist) {
                session.mkdir(testWorkingDirectory, null);
                testWorkingDirectoryExist = Sftps.existDirectory(session, testWorkingDirectory);
                logger.info("directory exist? {}", testWorkingDirectoryExist);
            }

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOs.close(session);
        }
    }
}
