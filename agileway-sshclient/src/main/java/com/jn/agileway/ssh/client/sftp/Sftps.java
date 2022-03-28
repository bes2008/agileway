package com.jn.agileway.ssh.client.sftp;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshConnectionConfig;
import com.jn.agileway.ssh.client.SshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnectionFactoryRegistry;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.PrimitiveArrays;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.io.file.PosixFilePermissions;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;

public class Sftps {
    private static final Logger logger = Loggers.getLogger(Sftps.class);
    private static final SshConnectionFactoryRegistry SSH_CONNECTION_FACTORY_REGISTRY;

    static {
        SSH_CONNECTION_FACTORY_REGISTRY = new SshConnectionFactoryRegistry();
        SSH_CONNECTION_FACTORY_REGISTRY.init();
    }

    /**
     * @return null 代表该路径不存在
     */
    public static FileType getFileType(SftpSession session, String filepath) throws SftpException {
        SftpFile file = null;
        try {
            FileAttrs fileAttrs = session.stat(filepath);
            return fileAttrs.getFileMode().getType();
        } catch (NoSuchFileSftpException ex) {
            return FileType.UNKNOWN;
        } finally {
            IOs.close(file);
        }
    }

    public static boolean exists(SftpSession session, String filepath) throws SftpException {
        return exists(session, filepath, null);
    }

    /**
     * 任何一种远程文件，都可用这种方式来判断
     */
    public static boolean exists(SftpSession session, String filepath, FileType fileType) throws SftpException {
        FileType type = getFileType(session, filepath);
        if (fileType == null) {
            return type != null;
        }
        return type == fileType;
    }

    /**
     * 判断是否存在 某个普通的文件
     *
     */
    public static boolean existFile(SftpSession session, String filepath) throws SftpException {
        return exists(session, filepath, FileType.REGULAR);
    }

    /**
     * 判断是否存在某个目录
     *
     */
    public static boolean existDirectory(SftpSession session, String directoryPath) throws SftpException {
        return exists(session, directoryPath, FileType.DIRECTORY);
    }

    public static void remove(SftpSession session, String path) throws SftpException {
        FileAttrs attrs = session.stat(path);
        if (attrs.isDirectory()) {
            removeDir(session, path, false);
        } else {
            session.rm(path);
        }
    }

    /**
     * 递归移除
     */
    public static void removeDir(final SftpSession session, String directory, boolean retainDirectory) throws SftpException {
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
     * copy local file to remote
     */
    public static void copy(@NonNull SftpSession session, @NonNull File file, @NotEmpty String remotePath) throws SftpException {
        Preconditions.checkArgument(file.exists(), "the file {} is not exist", file.getPath());
        if (file.isFile()) {
            if (remotePath.endsWith("/")) {
                copyFile(session, file, remotePath, null);
            } else {
                int index = remotePath.lastIndexOf("/");
                String remoteDir = null;
                String filename = null;
                if (index == -1) {
                    remoteDir = "~";
                    filename = remotePath;
                } else {
                    remoteDir = remotePath.substring(0, index);
                    filename = remotePath.substring(index + 1);
                }

                copyFile(session, file, remoteDir, filename);
            }
        } else {
            copyDir(session, file, remotePath);
        }
    }

    /**
     * copy local file to remote dir
     */
    public static int copyFile(@NonNull SftpSession session, @NonNull File file, @NotEmpty String remoteDir, @Nullable String newName) throws SftpException {
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

        } catch (Throwable ex) {
            throw new SftpException(ex);
        } finally {
            IOs.close(sftpFile);
            IOs.close(inputStream);
        }
        return sum;
    }

    /**
     * copy local directory to remote dir
     */
    public static void copyDir(final SftpSession session, File localDirectory, final String remoteDir) throws SftpException {
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

    public static void reverseCopy(final SftpSession session, final File localDirectory, final String path) throws IOException {
        FileType fileType = getFileType(session, path);
        if (fileType == FileType.REGULAR) {
            reverseCopyFile(session, localDirectory, path);
        } else if (fileType == FileType.DIRECTORY) {
            reverseCopyDirectory(session, localDirectory, path);
        }
    }

    /**
     * copy remote directory to local
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



    public static void chmod(final SftpSession session, String path, int permissions) throws IOException {
        FileAttrs attrs = session.stat(path);
        FileType fileType = attrs.getFileMode().getType();
        FileAttrs attrs2 = new FileAttrs();
        attrs2.setFileMode(FileMode.createFileMode(fileType, permissions));
        session.setStat(path, attrs2);
    }

    public static void chown(final SftpSession session, String path, int uid) throws IOException {
        FileAttrs attrs = session.stat(path);
        FileAttrs attrs2 = new FileAttrs();
        attrs2.setUid(uid);
        attrs2.setGid(attrs.getGid());
        session.setStat(path, attrs2);
    }

    public static void chgrp(final SftpSession session, String path, int gid) throws IOException {
        FileAttrs attrs = session.stat(path);
        FileAttrs attrs2 = new FileAttrs();
        attrs2.setUid(attrs.getUid());
        attrs2.setGid(gid);
        session.setStat(path, attrs2);
    }

    public static List<String> children(final SftpSession session, String directory) throws IOException {
        return Pipeline.of(session.listFiles(directory))
                .map(new Function<SftpResourceInfo, String>() {
                    @Override
                    public String apply(SftpResourceInfo resourceInfo) {
                        return resourceInfo.getName();
                    }
                }).asList();
    }

    public static PosixFilePermissions getPosixPermission(SftpFile sftpFile) throws IOException {
        FileAttrs attrs = sftpFile.getAttributes();

        int[] groupIds = sftpFile.getSession().getSshConnection().getGroupIds();
        boolean inGroup = Collects.newArrayList(PrimitiveArrays.wrap(groupIds)).contains(attrs.getGid());

        int uid = sftpFile.getSession().getSshConnection().getUid();
        boolean isOwner = attrs.getUid() == uid;

        return new PosixFilePermissions(attrs.getFileMode().getPermissionsMask(), isOwner, inGroup);
    }

    public static boolean isReadable(SftpFile sftpFile) throws IOException {
        return getPosixPermission(sftpFile).isReadable();
    }

    public static boolean isWritable(SftpFile sftpFile) throws IOException {
        return getPosixPermission(sftpFile).isWritable();
    }

    public static boolean isExecutable(SftpFile sftpFile) throws IOException {
        return getPosixPermission(sftpFile).isExecutable();
    }

    /**
     *
     * @param local the local file or directory
     * @param remote    the remote file or directory, format: {user}:{password}@{host}:[port]:{remotePath}
     * @param reverse reverse copy: from remote to local
     */
    public static void scp(String local, String remote, boolean reverse) throws Throwable{
        String[] segments = Strings.split(remote, "@");
        Preconditions.checkArgument(segments.length == 2, "illegal remote: {}", remote);
        String[] userAndPassword = Strings.split(segments[0],":");
        Preconditions.checkArgument(userAndPassword.length==2," user, password are required");
        String user = userAndPassword[0];
        String pswd = userAndPassword[1];
        String[] remoteMachinePath = Strings.split(segments[1],":");
        Preconditions.checkArgument(remoteMachinePath.length>=2, "host, port, remotePath are required");
        String remoteHost = remoteMachinePath[0];
        int remotePort = -1;
        String remotePath = null;
        if(remoteMachinePath.length>2){
            remotePort = Integer.parseInt(remoteMachinePath[1]);
            remotePath = remoteMachinePath[2];
        }
        else{
            remotePath = remoteMachinePath[1];
        }
        scp(local, remotePath, remoteHost, remotePort, user, pswd, reverse);
    }

    public static void scp(@NotEmpty String localPath, @NotEmpty String remotePath, @NotEmpty String remoteHost, int remotePort, @NotEmpty String remoteUser, String remotePswd, boolean reverse) throws IOException, SftpException {
        Preconditions.checkNotEmpty(localPath, "the local path is required");
        Preconditions.checkNotEmpty(remotePath, "the remote path is required");
        if (remotePort <= 0) {
            remotePort = 22;
        }

        SshConnectionFactory connectionFactory = SSH_CONNECTION_FACTORY_REGISTRY.getDefault();
        SshConnectionConfig connectionConfig = connectionFactory.newConfig();
        connectionConfig.setHost(remoteHost);
        connectionConfig.setPort(remotePort);
        connectionConfig.setUser(remoteUser);
        connectionConfig.setPassword(remotePswd);
        connectionConfig.setStrictHostKeyChecking(StrictHostKeyChecking.NO);

        SshConnection connection = null;
        SftpSession session = null;

        try {
            connection = connectionFactory.get(connectionConfig);
            session = connection.openSftpSession();
            if (reverse) {
                File localDir = new File(localPath);
                if (localDir.exists() && !localDir.isDirectory()) {
                    String error = StringTemplates.formatWithIndex("local path is not a directory: {}", localPath);
                    throw new IOException(error);
                }
                reverseCopy(session, localDir, remotePath);
            } else {
                File localFile = new File(localPath);
                if (!localFile.exists()) {
                    logger.error("local path is not exists: {}", localPath);
                    throw new FileNotFoundException(localPath);
                }
                copy(session, localFile, remotePath);
            }
        } finally {
            IOs.close(session);
            IOs.close(connection);
        }
    }

}
