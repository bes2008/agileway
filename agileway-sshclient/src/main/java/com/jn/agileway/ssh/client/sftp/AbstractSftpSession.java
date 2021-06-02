package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.io.IOException;
import java.util.List;

public abstract class AbstractSftpSession implements SftpSession {
    protected SshConnection connection;

    public SshConnection getSshConnection() {
        return connection;
    }

    public void setSshConnection(SshConnection connection) {
        this.connection = connection;
    }

    @Override
    public SftpFile open(String filepath, OpenMode openMode, FileAttrs attrs) throws IOException {
        return open(filepath, openMode.getCode(), attrs);
    }

    public List<SftpResourceInfo> listFiles(String directory) throws IOException{
        return listFiles(directory, null);
    }

    @Override
    public List<SftpResourceInfo> listFiles(String directory, Predicate<SftpResourceInfo> predicate) throws IOException {
        List<SftpResourceInfo> children = doListFiles(directory);
        return Pipeline.of(children)
                .filter(predicate == null ? Functions.<SftpResourceInfo>truePredicate() : predicate)
                .asList();
    }

    protected abstract List<SftpResourceInfo> doListFiles(String directory) throws IOException;
}
