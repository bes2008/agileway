package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.Sftps;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.test.BaseSshTests;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SftpTests extends BaseSshTests {
    private static final Logger logger = Loggers.getLogger(SftpTests.class);
    private SshConnectionFactoryRegistry registry = new SshConnectionFactoryRegistry();

    @Test
    public void testSftp_trilead_ssh2() throws IOException {
        _test(registry.get("trileadssh2"), StringTemplates.formatWithPlaceholder("/home/{}/Templates/test_sftp_trilead_ssh2", user));
    }


    @Test
    public void testSftp_jsch() throws IOException {
        _test(registry.get("jsch"), StringTemplates.formatWithPlaceholder("/home/{}/Templates/test_sftp_jsch", user));
    }

    @Test
    public void testSftp_sshj() throws IOException {
        _test(registry.get("sshj"), StringTemplates.formatWithPlaceholder("/home/{}/Templates/test_sftp_sshj", user));
    }

    @Test
    public void testSftp_synergy() throws IOException {
        _test(registry.get("synergy"), StringTemplates.formatWithPlaceholder("/home/{}/Templates/test_sftp_synergy", user));
    }

    void _test(SshConnectionFactory connectionFactory, final String testWorkingDirectory) throws IOException {
        SshConnectionConfig connectionConfig = connectionFactory.newConfig();
        //connectionConfig.setHost("192.168.234.128");
        connectionConfig.setHost(host);
        connectionConfig.setPort(sshPort);
        connectionConfig.setUser(user);
        connectionConfig.setPassword(password);

        SshConnection connection = null;
        SftpSession _session = null;
        try {
            connection = connectionFactory.get(connectionConfig);

            final SftpSession session = connection.openSftpSession();
            _session = session;

            FileAttrs attrs = session.stat(StringTemplates.formatWithPlaceholder("/home/{}", user));
            System.out.println(attrs);

            // 确保testWorkingDirectory 存在，并且是 empty的
            boolean testWorkingDirectoryExist = Sftps.existDirectory(session, testWorkingDirectory);
            logger.info("directory exist? {}", testWorkingDirectoryExist);
            if (!testWorkingDirectoryExist) {
                session.mkdir(testWorkingDirectory, null);
                testWorkingDirectoryExist = Sftps.existDirectory(session, testWorkingDirectory);
                logger.info("directory exist? {}", testWorkingDirectoryExist);
            }
            List<SftpResourceInfo> children = session.listFiles(testWorkingDirectory);
            if (!children.isEmpty()) {
                Sftps.removeDir(session, testWorkingDirectory, true);
            }

            // 拷贝 agileway-sshclient 模块下所有的文件到  testWorkingDirectory
            String localRootPath = SystemPropertys.getUserWorkDir()+"/..";
            File localRootDir = new File(localRootPath);
            Sftps.copy(session, localRootDir, testWorkingDirectory);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOs.close(_session);
            IOs.close(connection);
        }
    }

}
