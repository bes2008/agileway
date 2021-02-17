package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.langx.util.io.IOs;

import java.io.IOException;

public class Sftps {
    public static boolean existFile(SftpSession session, String filepath) throws IOException {
        SftpFile file = null;
        try {
            file = session.open(filepath, OpenMode.READ, null);
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
            file = session.open(directoryPath, OpenMode.READ, null);
            return true;
        } catch (NoSuchFileSftpException ex) {
            return false;
        } finally {
            IOs.close(file);
        }
    }
}
