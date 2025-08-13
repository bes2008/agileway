package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jn.agileway.ssh.client.*;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionConfig;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionFactory;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionConfig;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionConfig;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionFactory;
import com.jn.agileway.ssh.client.supports.command.SshCommandResponse;
import com.jn.agileway.ssh.test.BaseSshTests;
import com.jn.langx.util.logging.Loggers;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;

public class SshClients_CommandLineTests extends BaseSshTests {
    private static Logger logger = Loggers.getLogger(SshClients_CommandLineTests.class);


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
        connectionConfig.setHost(host); //pc
        // connectionConfig.setHost("192.168.1.70"); //work station
        connectionConfig.setPort(sshPort);
        connectionConfig.setUser(user);
        connectionConfig.setPassword(password);
        SshConnection connection = connectionFactory.get(connectionConfig);

        SshCommandResponse response = null;
        response = SshClients.exec(connection, "netstat -apn | grep 22");
        showResult(response);
        response = SshClients.exec(connection, "uname -a");
        showResult(response);

        response = SshClients.exec(connection, "ls -al");
        showResult(response);

        // 测试错误命令
        response = SshClients.exec(connection, "ls2 -al");
        showResult(response);
        connection.close();
    }

    private static void showResult(SshCommandResponse response) {
        if (response.hasError()) {
            System.out.println(response.getExitErrorMessage());
        } else {
            System.out.println(response.getResult());
        }
        System.out.println("========================");
    }
}
