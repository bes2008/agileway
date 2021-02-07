package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionConfig;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionFactory;
import com.jn.agileway.ssh.client.impl.jsch.JschGlobalProperties;
import com.jn.agileway.ssh.client.supports.command.SshCommandLineExecutor;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.DefaultExecuteResultHandler;
import com.jn.langx.commandline.streamhandler.OutputAsStringExecuteStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SshCommandLineExecutorTest {
    private static Logger logger = LoggerFactory.getLogger(SshCommandLineExecutorTest.class);

    public static void main(String[] args) throws Throwable {

        JschGlobalProperties jschGlobalProperties = new JschGlobalProperties();
        jschGlobalProperties.apply();


        JSch jsch = new JSch();
        jsch.setKnownHosts("known_hosts");

        JschConnectionFactory connectionFactory = new JschConnectionFactory();
        JschConnectionConfig connectionConfig = new JschConnectionConfig();
        connectionConfig.setHost("192.168.1.79");
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
