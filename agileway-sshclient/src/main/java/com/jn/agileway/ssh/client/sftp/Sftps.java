package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.langx.util.io.IOs;

import java.io.IOException;

public class Sftps {
    public static boolean existPath(SftpSession session, String filepath) throws IOException {
        SftpFile file = null;
        try {
            session.stat(filepath);
            return true;
        } catch (NoSuchFileSftpException ex) {
            return false;
        } finally {
            IOs.close(file);
        }
    }

    public static boolean existDirectory(SftpSession session, String directoryPath) throws IOException {
        SftpFile file = null;
        try {
            FileAttrs fileAttrs = session.stat(directoryPath);
            return fileAttrs.isDirectory();
        } catch (NoSuchFileSftpException ex) {
            return false;
        } finally {
            IOs.close(file);
        }
    }
}
