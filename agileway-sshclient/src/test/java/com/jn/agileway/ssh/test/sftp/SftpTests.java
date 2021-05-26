package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.trileadssh2.Ssh2ConnectionFactory;
import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SftpTests {
    private static final Logger logger = LoggerFactory.getLogger(SftpTests.class);
    private SshConnectionFactoryRegistry registry = new SshConnectionFactoryRegistry();

    @Test
    public void testSftp_trilead_ssh2() throws IOException {
        _test(registry.get("trileadssh2"), "/home/fangjinuo/Templates/test_sftp_trilead_ssh2");
    }


    @Test
    public void testSftp_jsch() throws IOException {
        _test(registry.get("jsch"), "/home/fangjinuo/Templates/test_sftp_jsch");
    }

    @Test
    public void testSftp_sshj() throws IOException {
        _test(registry.get("sshj"), "/home/fangjinuo/Templates/test_sftp_sshj");
    }

    void _test(SshConnectionFactory connectionFactory, final String testWorkingDirectory) throws IOException {
        SshConnectionConfig connectionConfig = connectionFactory.newConfig();
        //connectionConfig.setHost("192.168.234.128");
        connectionConfig.setHost("192.168.1.70");
        connectionConfig.setPort(22);
        connectionConfig.setUser("fangjinuo");
        connectionConfig.setPassword("fjn13570");

        SshConnection connection = null;
        SftpSession _session = null;
        try {
            connection = connectionFactory.get(connectionConfig);

            final SftpSession session = connection.openSftpSession();
            _session = session;

            FileAttrs attrs = session.stat("/home/fangjinuo");
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
            String localRootPath = SystemPropertys.getUserWorkDir();
            File localRootDir = new File(localRootPath);
            _copyDir(session, localRootDir, testWorkingDirectory);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOs.close(_session);
            IOs.close(connection);
        }
    }


    void _copyFile(SftpSession session, File file, String remoteDir) throws IOException {
        int length = Sftps.copyFile(session, file, remoteDir, null);
        String filepath = remoteDir + "/" + file.getName();
        // ganymed-ssh2 的 read 方法对 length 参数有这个限制
        if (length < 32786) {
            SftpFile sftpFile = session.open(filepath, OpenMode.READ, null);
            byte[] buffer = new byte[length];
            try {
                int readLength = sftpFile.read(0, buffer, 0, length);
                logger.info("read length == write length ? {}", length == readLength);
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                sftpFile.close();
            }
        }
        logger.info("canonical path: {}", session.canonicalPath(filepath));
    }

    void _copyDir(final SftpSession session, File localDirectory, final String remoteDir) throws IOException {
        boolean remoteDirExist = Sftps.existDirectory(session, remoteDir);
        if (!remoteDirExist) {
            session.mkdir(remoteDir, null);
        }
        Collects.forEach(localDirectory.listFiles(), new Consumer<File>() {
            @Override
            public void accept(File file) {
                String name = file.getName();
                try {
                    if (file.isFile()) {
                        _copyFile(session, file, remoteDir);
                    } else {
                        _copyDir(session, file, remoteDir + "/" + name);
                    }
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });

        List<SftpResourceInfo> ls = session.listFiles(remoteDir);
        Collects.forEach(ls, new Consumer<SftpResourceInfo>() {
            @Override
            public void accept(SftpResourceInfo sftpResourceInfo) {
                System.out.println(sftpResourceInfo);
            }
        });
    }
}
