package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.factory.Factory;

public interface SftpSessionFactory extends Factory<SshConnection, SftpSession> {
    @Override
    SftpSession get(SshConnection sshConnection);
}
