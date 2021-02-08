package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionConfig;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionFactory;
import com.jn.agileway.ssh.client.impl.j2ssh.J2sshConnectionConfig;
import com.jn.agileway.ssh.client.impl.j2ssh.J2sshConnectionFactory;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionConfig;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.supports.command.SshCommandLineExecutor;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.DefaultExecuteResultHandler;
import com.jn.langx.commandline.streamhandler.OutputAsStringExecuteStreamHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SshCommandLineExecutorTest {
    private static Logger logger = LoggerFactory.getLogger(SshCommandLineExecutorTest.class);


    @Test
    public void testJsch() throws Throwable {
        JschConnectionConfig connectionConfig = new JschConnectionConfig();
        testExec(new JschConnectionFactory(), connectionConfig);
    }


    @Test
    public void testGanymedSsh2() throws Throwable {
        testExec(new Ssh2ConnectionFactory(), new Ssh2ConnectionConfig());
    }


    @Test
    public void testTrileadSsh2() throws Throwable {
        testExec(new com.jn.agileway.ssh.client.impl.trileadssh2.Ssh2ConnectionFactory(), new com.jn.agileway.ssh.client.impl.trileadssh2.Ssh2ConnectionConfig());
    }

    @Test
    public void testSshj() throws Throwable {
        testExec(new SshjConnectionFactory(), new SshjConnectionConfig());
    }

    @Test
    public void testJ2ssh() throws Throwable {
        testExec(new J2sshConnectionFactory(), new J2sshConnectionConfig());
    }


    private void testExec(SshConnectionFactory connectionFactory, AbstractSshConnectionConfig connectionConfig) throws SshException, IOException {
        connectionConfig.setHost("192.168.234.128");
        // connectionConfig.setHost("192.168.1.79");
        connectionConfig.setPort(22);
        connectionConfig.setUser("fangjinuo");
        connectionConfig.setPassword("fjn13570");
        SshConnection connection = connectionFactory.get(connectionConfig);

        SshCommandLineExecutor executor = new SshCommandLineExecutor(connection);
        executor.setWorkingDirectory(new File("~/.java"));


        OutputAsStringExecuteStreamHandler output = new OutputAsStringExecuteStreamHandler();
        executor.setStreamHandler(output);

        executor.execute(CommandLine.parse("ifconfig"));
        showResult(executor);

        System.out.println("====================================");

        executor.execute(CommandLine.parse("ls -al"));
        showResult(executor);


        connection.close();
    }

    private static void showResult(SshCommandLineExecutor executor) {
        DefaultExecuteResultHandler resultHandler = (DefaultExecuteResultHandler) executor.getResultHandler();
        if (resultHandler.hasResult()) {
            Throwable exception = resultHandler.getException();
            if (exception != null) {
                logger.error(exception.getMessage(), exception);
            } else {
                OutputAsStringExecuteStreamHandler output = (OutputAsStringExecuteStreamHandler) executor.getStreamHandler();
                String str = output.getOutputContent();
                logger.info(str);
            }
        }
    }
}
