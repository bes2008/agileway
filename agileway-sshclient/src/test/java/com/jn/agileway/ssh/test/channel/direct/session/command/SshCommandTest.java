package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.impl.jsch.JschLoggerToSlf4jLogger;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class SshCommandTest {
    private static final Logger logger = Loggers.getLogger(SshCommandTest.class);

    @Test
    public void testUseJschAPI() throws Throwable{
        String password = "fjn13570";
        String username = "fangjinuo";
        //  String host = "192.168.1.79";
        String host = "192.168.234.128";
        int port = 22;

        JSch.setConfig("StrictHostKeyChecking","false");

        JSch jsch = new JSch();
        // jsch.setKnownHosts("known_hosts");
        JSch.setLogger(new JschLoggerToSlf4jLogger());
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.connect();


        executeAndDump(session, "route");
        executeAndDump(session, "ifconfig");
        executeAndDump(session, "cd ~/.java;ls -al");
        session.disconnect();
    }

    private void executeAndDump(Session session, String command) throws JSchException, IOException {
        logger.info("\n====== start execute: {} ======", command);
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();
        logger.info("isClosed: {}", channel.isClosed());
        logger.info("isConnected: {}", channel.isConnected());
        InputStream errorStream = channel.getErrStream();
        InputStream outStream = channel.getInputStream();
        String error = IOs.readAsString(errorStream);
        logger.info(error);
        String content = IOs.readAsString(outStream);
        logger.info(content);
        channel.disconnect();
        logger.info("====== finish execute: {} ======", command);
    }

    @Test
    public void testUseAgilewayJschAPI() throws Throwable{

        SshConnectionFactoryRegistry registry = new SshConnectionFactoryRegistry();
        SshConnectionFactory connectionFactory = registry.get("jsch");
        SshConnectionConfig connectionConfig = connectionFactory.newConfig();

        String password = "fjn13570";
        String username = "fangjinuo";
        //  String host = "192.168.1.79";
        String host = "192.168.234.128";
        int port = 22;

        connectionConfig.setHost(host);
        connectionConfig.setPort(port);
        connectionConfig.setPassword(password);
        connectionConfig.setUser(username);
        SshConnection connection = connectionFactory.get(connectionConfig);



        executeAndDump(connection, "route");
        executeAndDump(connection, "ifconfig");
        executeAndDump(connection, "cd ~/.java;ls -al");
        connection.close();
    }

    private void executeAndDump(SshConnection connection, String command) throws JSchException, IOException {
        logger.info("\n====== start execute: {} ======", command);
        SessionedChannel channel = connection.openSession();

        channel.exec(command);
        InputStream outStream = channel.getInputStream();
        String content = IOs.readAsString(outStream);
        logger.info(content);
        channel.close();
        logger.info("====== finish execute: {} ======", command);
    }


}
