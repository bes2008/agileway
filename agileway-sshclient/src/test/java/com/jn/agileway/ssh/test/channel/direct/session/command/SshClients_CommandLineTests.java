package com.jn.agileway.ssh.test.channel.direct.session.command;

import com.jn.agileway.ssh.client.*;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionConfig;
import com.jn.agileway.ssh.client.impl.ganymedssh2.Ssh2ConnectionFactory;
import com.jn.agileway.ssh.client.impl.j2ssh.J2sshConnectionConfig;
import com.jn.agileway.ssh.client.impl.j2ssh.J2sshConnectionFactory;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionConfig;
import com.jn.agileway.ssh.client.impl.jsch.JschConnectionFactory;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionConfig;
import com.jn.agileway.ssh.client.impl.sshj.SshjConnectionFactory;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionConfig;
import com.jn.agileway.ssh.client.impl.synergy.SynergyConnectionFactory;
import com.jn.agileway.ssh.client.supports.command.SshCommandResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SshClients_CommandLineTests {
    private static Logger logger = LoggerFactory.getLogger(SshClients_CommandLineTests.class);


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

    /**
     * J2ssh 问题较多，目前无法进行测试
     * @throws Throwable
     */
    @Test
    public void testJ2ssh() throws Throwable {
        testExec(new J2sshConnectionFactory(), new J2sshConnectionConfig());
    }


    private void testExec(SshConnectionFactory connectionFactory, AbstractSshConnectionConfig connectionConfig) throws SshException, IOException {
        //connectionConfig.setHost("192.168.234.128");
        connectionConfig.setHost("192.168.1.70");
        connectionConfig.setPort(22);
        connectionConfig.setUser("fangjinuo");
        connectionConfig.setPassword("fjn13570");
        SshConnection connection = connectionFactory.get(connectionConfig);

        SshCommandResponse response = null;

        response = SshClients.exec(connection, "ifconfig");
        showResult(response);

        System.out.println("====================================");

        response = SshClients.exec(connection, "ls -al");
        showResult(response);

        // 测试错误命令
        response = SshClients.exec(connection, "ls2 -al");
        showResult(response);
        connection.close();
    }

    private static void showResult(SshCommandResponse response) {
        if (response.hasError()) {
            logger.error(response.getExitErrorMessage());
        } else {
            logger.info(response.getResult());
        }
    }
}
