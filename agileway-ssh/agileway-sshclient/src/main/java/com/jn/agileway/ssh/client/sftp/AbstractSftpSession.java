package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

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
    public SftpFile open(String filepath, OpenMode openMode, FileAttrs attrs) throws SftpException {
        return open(filepath, openMode.getCode(), attrs);
    }

    public List<SftpResourceInfo> listFiles(String directory) throws SftpException {
        return listFiles(directory, null);
    }

    public void mkdirs(String directory, FileAttrs attributes) throws SftpException {
        String[] segments = Strings.split(directory, "/");
        boolean isAbsolutePath = Strings.startsWith(directory, "/");
        String path = isAbsolutePath ? "/" : "";
        for (int i = 0; i < segments.length; i++) {
            path += segments[i] + "/";
            if (!Sftps.exists(this, path)) {
                mkdir(path, attributes);
            }
        }
    }

    @Override
    public List<SftpResourceInfo> listFiles(String directory, Predicate<SftpResourceInfo> predicate) throws SftpException {
        List<SftpResourceInfo> children = doListFiles(directory);
        return Pipeline.of(children)
                .filter(predicate == null ? Functions.<SftpResourceInfo>truePredicate() : predicate)
                .asList();
    }

    protected abstract List<SftpResourceInfo> doListFiles(String directory) throws SftpException;
}
