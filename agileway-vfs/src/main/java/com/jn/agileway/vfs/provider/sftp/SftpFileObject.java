package com.jn.agileway.vfs.provider.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.agileway.ssh.client.sftp.Sftps;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.util.collection.Collects;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SftpFileObject extends AbstractFileObject<SftpFileSystem> {
    private FileAttrs fileAttrs;

    public SftpFileObject(AbstractFileName name, SftpFileSystem fs) {
        super(name, fs);
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
            fileAttrs = getSftpSession().stat(getName().getPath());
        }
        return fileAttrs;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        getSftpSession().open(getName().getPath(), OpenMode.READ, getFileAttrs());
        return null;
    }

    @Override
    protected FileType doGetType() throws Exception {
        com.jn.agileway.ssh.client.sftp.attrs.FileType fileType = getFileAttrs().getFileType();
        FileType ft = FileType.IMAGINARY;
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
                ft = FileType.IMAGINARY;
                break;
        }
        return ft;
    }

    @Override
    protected String[] doListChildren() throws Exception {
        List<String> children = Sftps.children(getSftpSession(), getName().getPath());
        return Collects.toArray(children, String[].class);
    }
}
