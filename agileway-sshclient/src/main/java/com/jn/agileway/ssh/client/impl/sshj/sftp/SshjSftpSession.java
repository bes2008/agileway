package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.SftpFile;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttributes;
import com.jn.agileway.ssh.client.sftp.filter.SftpFileFilter;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.util.List;

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
    public SftpFile open(String filepath, OpenMode openMode, FileAttributes attrs) {
        return null;
    }

    @Override
    public SftpFile open(String filepath, int openMode, FileAttributes attrs) {
        return null;
    }

    @Override
    public void createSymlink(String src, String target) throws IOException {

    }

    @Override
    public String readLink(String path) throws IOException {
        return null;
    }

    @Override
    public String canonicalPath(String path) throws IOException {
        return null;
    }

    @Override
    public void close(SftpFile file) {

    }

    @Override
    public int read(SftpFile file, long fileOffset, byte[] buffer, int bufferOffset, int length) {
        return 0;
    }

    @Override
    public void write(SftpFile file, long fileOffset, byte[] data, int offset, int length) {

    }

    @Override
    public FileAttributes stat(String filepath) throws IOException {
        return null;
    }

    @Override
    public FileAttributes lstat(String filepath) throws IOException {
        return null;
    }

    @Override
    public FileAttributes fstat(SftpFile file) throws IOException {
        return null;
    }

    @Override
    public void setStat(String path, FileAttributes attrs) throws IOException {

    }

    @Override
    public List<SftpFile> listFiles(SftpFile file, SftpFileFilter filter) throws IOException {
        return null;
    }

    @Override
    public void mkdir(String directory, FileAttributes attributes) throws IOException {

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
