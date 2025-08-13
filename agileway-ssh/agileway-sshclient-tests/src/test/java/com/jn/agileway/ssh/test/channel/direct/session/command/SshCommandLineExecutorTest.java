package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.DefaultExecuteResultHandler;
import com.jn.agileway.cmd.streamhandler.OutputAsStringExecuteStreamHandler;
import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionConfig;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionFactory;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionConfig;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionConfig;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionFactory;
import com.jn.agileway.ssh.client.supports.command.executor.SshCommandLineExecutor;
import com.jn.agileway.ssh.test.BaseSshTests;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.logging.Loggers;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class SshCommandLineExecutorTest extends BaseSshTests {
    private static Logger logger = Loggers.getLogger(SshCommandLineExecutorTest.class);


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
    public void testSynergy() throws Throwable {
        testExec(new SynergyConnectionFactory(), new SynergyConnectionConfig());
    }


    private void testExec(SshConnectionFactory connectionFactory, AbstractSshConnectionConfig connectionConfig) throws SshException, IOException {
        //connectionConfig.setHost("192.168.234.128");
        connectionConfig.setHost(host);
        connectionConfig.setPort(sshPort);
        connectionConfig.setUser(user);
        connectionConfig.setPassword(password);
        SshConnection connection = connectionFactory.get(connectionConfig);

        SshCommandLineExecutor executor = new SshCommandLineExecutor(connection);
        executor.setWorkingDirectory(new File("~"));


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
                String error = StringTemplates.formatWithPlaceholder("{}, {}", exception.getMessage(), exception);
                System.err.println(error);
            } else {
                OutputAsStringExecuteStreamHandler output = (OutputAsStringExecuteStreamHandler) executor.getStreamHandler();
                String str = output.getOutputContent();
                System.out.println(str);
            }
        }
    }
}
