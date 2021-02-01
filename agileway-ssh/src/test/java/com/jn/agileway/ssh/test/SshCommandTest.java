package com.jn.agileway.ssh.test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.jsch.ChannelType;
import com.jn.agileway.ssh.jsch.JschGlobalProperties;
import com.jn.agileway.ssh.jsch.JschProperties;
import com.jn.langx.util.io.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class SshCommandTest {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandTest.class);

    public static void main(String[] args) throws JSchException, IOException {
        JschGlobalProperties jschGlobalProperties = new JschGlobalProperties();
        jschGlobalProperties.apply();


        JschProperties jschProperties = new JschProperties();
        jschProperties.setPassword("fjn13570");
        jschProperties.setUsername("fangjinuo");
        jschProperties.setHost("192.168.1.79");
        jschProperties.setPort(22);


        JSch jsch = new JSch();
        jsch.setKnownHosts("known_hosts");

        Session session = jsch.getSession(jschProperties.getUsername(), jschProperties.getHost(), jschProperties.getPort());
        session.setPassword(jschProperties.getPassword());
        session.connect();


        executeAndDump(session, "route");
        executeAndDump(session, "ifconfig");


        session.disconnect();
    }


    private static void executeAndDump(Session session, String command) throws JSchException, IOException {
        logger.info("\n====== start execute: {} ======", command);
        ChannelExec channel = (ChannelExec) session.openChannel(ChannelType.EXEC.getName());
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



}
