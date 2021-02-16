package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.filter.SftpFileFilter;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SshjSftpSession implements SftpSession {
    private SFTPClient sftpClient;

    public SshjSftpSession(SFTPClient client) {
        this.sftpClient = client;
    }

    @Override
    public int getProtocolVersion() {
        return sftpClient.version();
    }

    @Override
    public SftpFile open(String filepath, OpenMode openMode, FileAttrs attrs) throws IOException {
        return open(filepath, openMode.getCode(), attrs);
    }

    @Override
    public SftpFile open(String filepath, int openMode, final FileAttrs attrs) throws IOException {
        Set<net.schmizz.sshj.sftp.OpenMode> openModes = SshjSftps.toSshjOpenModeSet(openMode);
        net.schmizz.sshj.sftp.FileAttributes attributes = SshjSftps.toFileAttributes(attrs);
        RemoteFile remoteFile = sftpClient.open(filepath, openModes, attributes);
        SshjSftpFile file = new SshjSftpFile(this, filepath);
        file.setRemoteFile(remoteFile);
        return file;
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {
        sftpClient.symlink(src, target);
    }

    @Override
    public String readLink(String path) throws IOException {
        return sftpClient.readlink(path);
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        return sftpClient.canonicalize(path);
    }

    @Override
    public void close(SftpFile file) {
        file.close();
    }

    @Override
    public int read(SftpFile file, long fileOffset, byte[] buffer, int bufferOffset, int length) {
        return 0;
    }

    @Override
    public void write(SftpFile file, long fileOffset, byte[] data, int offset, int length) {

    }

    @Override
    public FileAttrs stat(String filepath) throws IOException {
        net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.stat(filepath);
        return SshjSftps.fromFileAttributes(fileAttributes);
    }

    @Override
    public FileAttrs lstat(String filepath) throws IOException {
        net.schmizz.sshj.sftp.FileAttributes fileAttributes = sftpClient.lstat(filepath);
        return SshjSftps.fromFileAttributes(fileAttributes);
    }

    @Override
    public FileAttrs fstat(SftpFile file) throws IOException {
        return file.getAttributes();
    }

    @Override
    public void setStat(String path, FileAttrs attrs) throws IOException {
        sftpClient.setattr(path, SshjSftps.toFileAttributes(attrs));
    }

    @Override
    public List<SftpFile> listFiles(String directory, SftpFileFilter filter) throws IOException {
        return null;
    }

    @Override
    public void mkdir(String directory, FileAttrs attributes) throws IOException {

    }

    @Override
    public void rmdir(String directory) throws IOException {

    }

    @Override
    public void rm(String filepath) throws IOException {

    }

    @Override
    public void mv(String oldFilepath, String newFilepath) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
