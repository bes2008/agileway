package com.jn.agileway.vfs.provider.sftp;

import com.jn.agileway.ssh.client.sftp.*;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.file.FilePermission;
import com.jn.langx.util.io.file.PosixFilePermissions;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.util.FileObjectUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.List;

public class SftpFileObject extends AbstractFileObject<SftpFileSystem> {
    private FileAttrs fileAttrs;
    private final String relPath;

    public SftpFileObject(AbstractFileName name, SftpFileSystem fs) throws FileSystemException {
        super(name, fs);
        relPath = UriParser.decode(fs.getRootName().getRelativeName(name));
    }


    @Override
    protected long doGetContentSize() throws Exception {
        return getFileAttrs().getSize();
    }

    private SftpSession getSftpSession() {
        return getAbstractFileSystem().getSftpSession();
    }

    private FileAttrs getFileAttrs() throws IOException {
        if (this.fileAttrs == null) {
            fileAttrs = getSftpSession().stat(relPath);
        }
        return fileAttrs;
    }

    @Override
    protected void doCreateFolder() throws Exception {
        getSftpSession().mkdir(relPath, null);
    }

    @Override
    public FileType getType() throws FileSystemException {
        try {
            return super.getType();
        } catch (FileSystemException ex) {
            Throwable cause = ex.getCause();
            if ((cause instanceof NoSuchFileSftpException) || (cause instanceof FileNotFoundException) || (cause instanceof NoSuchFileException)) {
                return FileType.IMAGINARY;
            }
            throw ex;
        }
    }

    @Override
    protected void doAttach() throws Exception {
        // ignore it
    }

    @Override
    protected void doDetach() throws Exception {
        fileAttrs = null;
    }

    @Override
    protected void doDelete() throws Exception {
        if(isFile()){
            getSftpSession().rm(relPath);
        }else{
            getSftpSession().rmdir(relPath);
        }
    }

    @Override
    protected long doGetLastModifiedTime() throws Exception {
        return getFileAttrs().getModifyTime() * 1000L;
    }

    @Override
    protected boolean doIsExecutable() throws Exception {
        return Sftps.isExecutable(getSftpSession().open(relPath, OpenMode.READ, null));
    }

    @Override
    protected boolean doIsReadable() throws Exception {
        return Sftps.isReadable(getSftpSession().open(relPath, OpenMode.READ, null));
    }

    @Override
    protected boolean doIsWriteable() throws Exception {
        return Sftps.isWritable(getSftpSession().open(relPath, OpenMode.READ, null));
    }

    @Override
    protected FileObject[] doListChildrenResolved() throws Exception {
        return super.doListChildrenResolved();
    }

    @Override
    protected void doRename(FileObject newFile) throws Exception {
        final SftpFileObject newSftpFileObject = (SftpFileObject) FileObjectUtils.getAbstractFileObject(newFile);
        getSftpSession().mv(relPath, newSftpFileObject.relPath);
    }

    private void flushStat() throws IOException {
        getSftpSession().setStat(relPath, getFileAttrs());
    }

    @Override
    protected boolean doIsHidden() throws Exception {
        if (exists()) {
            if (isFolder()) {
                return true;
            } else {
                String[] segments = Strings.split(relPath, "/");
                String name = segments[segments.length - 1];
                if (name.startsWith(".") && !name.startsWith("..")) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    protected boolean doIsSameFile(final FileObject destFile) throws FileSystemException {
        if (destFile.getFileSystem() == this.getFileSystem() && (destFile instanceof SftpFileObject)) {
            SftpFileObject dest = (SftpFileObject) destFile;
            return this.relPath.equals(dest.relPath);
        }
        return false;
    }

    @Override
    protected boolean doSetExecutable(boolean executable, boolean ownerOnly) throws Exception {
        PosixFilePermissions posixFilePermissions = Sftps.getPosixPermission(getSftpSession().open(relPath, OpenMode.READ, null));
        int oldPerm = posixFilePermissions.getPermissions();
        if (executable) {
            posixFilePermissions.addPermission(FilePermission.USR_X);
        } else {
            posixFilePermissions.removePermission(FilePermission.USR_X);
        }

        if (!ownerOnly) {
            posixFilePermissions.addPermission(FilePermission.GRP_X);
            posixFilePermissions.addPermission(FilePermission.OTH_X);
        }

        if (posixFilePermissions.getPermissions() == oldPerm) {
            return true;
        }
        FileMode fileMode = FileMode.createFileMode(getFileAttrs().getFileMode().getType(), posixFilePermissions.getPermissions());
        getFileAttrs().setFileMode(fileMode);

        flushStat();
        return true;
    }

    @Override
    protected boolean doSetLastModifiedTime(long modtime) throws Exception {
        getFileAttrs().setModifyTime((int) (modtime / 1000L));
        flushStat();
        return true;
    }

    @Override
    protected boolean doSetReadable(boolean readable, boolean ownerOnly) throws Exception {
        PosixFilePermissions posixFilePermissions = Sftps.getPosixPermission(getSftpSession().open(relPath, OpenMode.READ, null));
        int oldPerm = posixFilePermissions.getPermissions();
        if (readable) {
            posixFilePermissions.addPermission(FilePermission.USR_R);
        } else {
            posixFilePermissions.removePermission(FilePermission.USR_R);
        }

        if (!ownerOnly) {
            posixFilePermissions.addPermission(FilePermission.GRP_R);
            posixFilePermissions.addPermission(FilePermission.OTH_R);
        }

        if (posixFilePermissions.getPermissions() == oldPerm) {
            return true;
        }
        FileMode fileMode = FileMode.createFileMode(getFileAttrs().getFileMode().getType(), posixFilePermissions.getPermissions());
        getFileAttrs().setFileMode(fileMode);

        flushStat();
        return true;
    }

    @Override
    protected boolean doSetWritable(boolean writable, boolean ownerOnly) throws Exception {
        PosixFilePermissions posixFilePermissions = Sftps.getPosixPermission(getSftpSession().open(relPath, OpenMode.READ, null));
        int oldPerm = posixFilePermissions.getPermissions();
        if (writable) {
            posixFilePermissions.addPermission(FilePermission.USR_W);
        } else {
            posixFilePermissions.removePermission(FilePermission.USR_W);
        }

        if (!ownerOnly) {
            posixFilePermissions.addPermission(FilePermission.GRP_W);
            posixFilePermissions.addPermission(FilePermission.OTH_W);
        }

        if (posixFilePermissions.getPermissions() == oldPerm) {
            return true;
        }
        FileMode fileMode = FileMode.createFileMode(getFileAttrs().getFileMode().getType(), posixFilePermissions.getPermissions());
        getFileAttrs().setFileMode(fileMode);

        flushStat();
        return true;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        SftpFile sftpFile = getSftpSession().open(relPath, OpenMode.READ, getFileAttrs());
        return new SftpFileInputStream(sftpFile);
    }


    @Override
    protected OutputStream doGetOutputStream(boolean bAppend) throws Exception {
        try {
            if (bAppend) {
                SftpFile sftpFile = getSftpSession().open(relPath, OpenMode.APPEND, fileAttrs);
                return new SftpFileOutputStream(sftpFile, -1, fileAttrs.getSize());
            } else {
                SftpFile sftpFile = getSftpSession().open(relPath, OpenMode.WRITE, fileAttrs);
                return new SftpFileOutputStream(sftpFile);
            }
        } catch (SftpException ex) {
            throw new FileSystemException(ex);
        }
    }

    @Override
    protected FileType doGetType() throws Exception {
        com.jn.agileway.ssh.client.sftp.attrs.FileType fileType = getFileAttrs().getFileType();
        FileType ft = FileType.IMAGINARY;
        if (fileType != null) {
            switch (fileType) {
                case REGULAR:
                    ft = FileType.FILE;
                    break;
                case DIRECTORY:
                    ft = FileType.FOLDER;
                    break;
                case SYMBOLIC_LINK:
                    ft = FileType.FILE_OR_FOLDER;
                    break;
                case CHAR_SPECIAL:
                case FIFO_SPECIAL:
                case SOCKET_SPECIAL:
                case BLOCK_SPECIAL:
                case UNKNOWN:
                    break;
                default:
                    break;
            }
        }
        return ft;
    }

    @Override
    protected String[] doListChildren() throws Exception {
        List<String> children = Sftps.children(getSftpSession(), relPath);
        return Collects.toArray(children, String[].class);
    }

    @Override
    public String toString() {
        FileAttrs attrs = null;
        try {
            attrs = getFileAttrs();
        } catch (Throwable ex) {
            // ignore it
        }
        return relPath + " " + attrs;
    }
}
