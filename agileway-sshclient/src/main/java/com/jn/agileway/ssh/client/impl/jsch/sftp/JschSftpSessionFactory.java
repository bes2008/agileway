package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.jsch.JschConnection;
import com.jn.agileway.ssh.client.sftp.SftpSessionFactory;

public class JschSftpSessionFactory implements SftpSessionFactory<JschSftpSession> {
    @Override
    public JschSftpSession get(SshConnection sshConnection) {
        JschConnection conn = (JschConnection) sshConnection;
        try {
            ChannelSftp channel = (ChannelSftp) conn.getDelegate().openChannel("sftp");
            channel.connect();
            return new JschSftpSession(channel);
        } catch (JSchException ex) {
            throw new SshException(ex);
        }
    }
}
