package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**
     * copy local file to remote dir
     *
     * @param session
     * @param file
     * @param remoteDir
     * @return
     * @throws IOException
     */
    public static int copyFile(@NonNull SftpSession session, @NonNull File file, @NotEmpty String remoteDir, @Nullable String newName) throws IOException {
        boolean remoteDirExist = Sftps.existDirectory(session, remoteDir);
        if (!remoteDirExist) {
            session.mkdir(remoteDir, null);
        }
        String name = Emptys.isEmpty(newName) ? file.getName() : newName;
        String filepath = remoteDir + "/" + name;

        FileInputStream inputStream = null;
        SftpFile sftpFile = null;
        int sum = 0;
        try {
            sftpFile = session.open(filepath, OpenMode.WRITE, null);
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
                sftpFile.write(sum, buffer, 0, readLength);
                sum += readLength;
            }
        } finally {
            IOs.close(sftpFile);
            IOs.close(inputStream);
        }
        return sum;
    }

    /**
     * copy local directory to remote dir
     *
     * @param session
     * @param localDirectory
     * @param remoteDir
     * @return
     * @throws IOException
     */
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
                        copyFile(session, file, remoteDir, null);
                    } else {
                        copyDir(session, file, remoteDir + "/" + name);
                    }
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
    }

    /**
     * copy remote directory to local
     *
     * @param session
     * @param localDirectory
     * @param remoteFile
     */
    public static int reverseCopyFile(final SftpSession session, final File localDirectory, final String remoteFile) throws IOException {
        if (!existFile(session, remoteFile)) {
            logger.error("remote file is not exist: {}", remoteFile);
            return -1;
        }
        Files.makeDirs(localDirectory);

        FileOutputStream fout = null;
        SftpFile sftpFile = null;
        int sum = 0;
        try {
            fout = new FileOutputStream(localDirectory);
            sftpFile = session.open(remoteFile, OpenMode.READ, null);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = sftpFile.read(sum, buffer, 0, buffer.length)) != -1) {
                fout.write(buffer, 0, readLength);
                sum += readLength;
            }
        } finally {
            IOs.close(sftpFile);
            IOs.close(fout);
        }
        return sum;
    }

    public static void reverseCopyDirectory(final SftpSession session, final File localDirectory, final String remoteDirectory) throws IOException {
        if (!existDirectory(session, remoteDirectory)) {
            logger.error("remote directory is not exist: {}", remoteDirectory);
            return;
        }
        Files.makeDirs(localDirectory);
        Collects.forEach(session.listFiles(remoteDirectory), new Consumer<SftpResourceInfo>() {
            @Override
            public void accept(SftpResourceInfo sftpResourceInfo) {
                String name = sftpResourceInfo.getName();
                try {
                    if (sftpResourceInfo.getAttrs().isDirectory()) {
                        reverseCopyDirectory(session, new File(localDirectory, name), sftpResourceInfo.getPath());
                    } else {
                        reverseCopyFile(session, localDirectory, sftpResourceInfo.getPath());
                    }
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
    }
}
