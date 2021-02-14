package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.factory.Factory;

public interface SftpSessionFactory<SESSION extends SftpSession> extends Factory<SshConnection, SESSION> {
    @Override
    SESSION get(SshConnection sshConnection);
}
