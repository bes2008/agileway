package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Sftps {
    private static final Logger logger = LoggerFactory.getLogger(Sftps.class);

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

    public static void remove(SftpSession session, String path) throws IOException {
        FileAttrs attrs = session.stat(path);
        if (attrs.isDirectory()) {
            removeDir(session, path, false);
        } else {
            session.rm(path);
        }
    }

    public static void removeDir(final SftpSession session, String directory, boolean retainDirectory) throws IOException {
        List<SftpResourceInfo> children = session.listFiles(directory);
        if (!children.isEmpty()) {
            Collects.forEach(children, new Consumer<SftpResourceInfo>() {
                @Override
                public void accept(SftpResourceInfo sftpResourceInfo) {
                    try {
                        remove(session, sftpResourceInfo.getPath());
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });
        }
        if (!retainDirectory) {
            session.rmdir(directory);
        }
    }
}
