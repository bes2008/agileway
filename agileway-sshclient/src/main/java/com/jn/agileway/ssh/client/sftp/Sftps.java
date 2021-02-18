package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Sftps {
    private static final Logger logger = LoggerFactory.getLogger(Sftps.class);

    /**
     * @param session
     * @param filepath
     * @return null 代表该路径不存在
     */
    public static FileType getFileType(SftpSession session, String filepath) throws IOException {
        SftpFile file = null;
        try {
            FileAttrs fileAttrs = session.stat(filepath);
            return fileAttrs.getFileMode().getType();
        } catch (NoSuchFileSftpException ex) {
            return null;
        } finally {
            IOs.close(file);
        }
    }

    public static boolean exist(SftpSession session, String filepath) throws IOException {
        return exist(session, filepath, null);
    }

    /**
     * 任何一种远程文件，都可用这种方式来判断
     *
     * @param session
     * @param filepath
     * @return
     * @throws IOException
     */
    public static boolean exist(SftpSession session, String filepath, FileType fileType) throws IOException {
        FileType type = getFileType(session, filepath);
        if (fileType == null) {
            return type != null;
        }
        return type == fileType;
    }

    /**
     * 判断是否存在 某个普通的文件
     *
     * @param session
     * @param filepath
     * @return
     * @throws IOException
     */
    public static boolean existFile(SftpSession session, String filepath) throws IOException {
        return exist(session, filepath, FileType.REGULAR);
    }

    /**
     * 判断是否存在某个目录
     *
     * @param session
     * @param directoryPath
     * @return
     * @throws IOException
     */
    public static boolean existDirectory(SftpSession session, String directoryPath) throws IOException {
        return exist(session, directoryPath, FileType.DIRECTORY);
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


    public static int copyFile(SftpSession session, File file, String remoteDir) throws IOException {
        boolean remoteDirExist = Sftps.existDirectory(session, remoteDir);
        if (!remoteDirExist) {
            session.mkdir(remoteDir, null);
        }
        String name = file.getName();
        String filepath = remoteDir + "/" + name;
        SftpFile sftpFile = session.open(filepath, OpenMode.WRITE, null);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] fileData = IOs.toByteArray(inputStream);
        IOs.close(inputStream);
        try {
            sftpFile.write(0, fileData, 0, fileData.length);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            sftpFile.close();
        }
        return fileData.length;
    }

    public static void copyDir(final SftpSession session, File localDirectory, final String remoteDir) throws IOException {
        boolean remoteDirExist = Sftps.existDirectory(session, remoteDir);
        if (!remoteDirExist) {
            session.mkdir(remoteDir, null);
        }
        Collects.forEach(localDirectory.listFiles(), new Consumer<File>() {
            @Override
            public void accept(File file) {
                String name = file.getName();
                try {
                    if (file.isFile()) {
                        copyFile(session, file, remoteDir);
                    } else {
                        copyDir(session, file, remoteDir + "/" + name);
                    }
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });

    }

}
