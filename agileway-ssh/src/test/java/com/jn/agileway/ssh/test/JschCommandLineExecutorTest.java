package com.jn.agileway.ssh.test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.jsch.JschGlobalProperties;
import com.jn.agileway.ssh.jsch.JschProperties;
import com.jn.agileway.ssh.jsch.exec.JschCommandLineExecutor;
import com.jn.langx.commandline.CommandLine;
import com.jn.langx.commandline.ExecuteException;
import com.jn.langx.commandline.ExecuteResultHandler;
import com.jn.langx.commandline.streamhandler.OutputAsStringExecuteStreamHandler;

import java.io.IOException;

public class JschCommandLineExecutorTest {
    public static void main(String[] args) throws IOException, JSchException {

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


        JschCommandLineExecutor executor = new JschCommandLineExecutor(session);
        executor.setStreamHandler(new OutputAsStringExecuteStreamHandler());


        CommandLine commandLine = CommandLine.parse("ifconfig");

        executor.execute(commandLine, new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {

            }

            @Override
            public void onProcessFailed(ExecuteException e) {

            }
        });

        session.disconnect();
    }
}
