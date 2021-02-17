package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.sftp.SshjSftpSessionFactory;
import com.jn.agileway.ssh.client.sftp.*;
import com.jn.langx.util.Objs;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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

        final SftpSession session = sessionFactory.get(connection);
        try {
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
                Collects.forEach(children, new Consumer<SftpResourceInfo>() {
                    @Override
                    public void accept(SftpResourceInfo sftpResourceInfo) {
                        try {
                            session.rm(sftpResourceInfo.getPath());
                        } catch (Throwable ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                });
            }

            // 拷贝 agileway-sshclient 模块下所有的文件到  testWorkingDirectory
            String localRootPath = SystemPropertys.getUserWorkDir();
            File localRootDir = new File(localRootPath);
            _copyDir(session, localRootDir, testWorkingDirectory);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            IOs.close(session);
        }
    }

    void _copyFile(SftpSession session, File file, String remoteDir) throws IOException {
        boolean remoteDirExist = Sftps.existDirectory(session, remoteDir);
        if (!remoteDirExist) {
            session.mkdir(remoteDir, null);
        }
        String name = file.getName();
        String filepath = remoteDir + "/" + name;
        SftpFile sftpFile = session.open(filepath, OpenMode.WRITE, null);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] fileData = IOs.toByteArray(inputStream);
        IOs.close(inputStream);
        try {
            sftpFile.write(0, fileData, 0, fileData.length);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            sftpFile.close();
        }


        sftpFile = session.open(filepath, OpenMode.READ, null);
        byte[] buffer = new byte[fileData.length];
        try {
            int readLength = sftpFile.read(0, buffer, 0, fileData.length);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            sftpFile.close();
        }
        logger.info("{}:{}", file.getPath(), Objs.deepEquals(buffer, fileData));
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
