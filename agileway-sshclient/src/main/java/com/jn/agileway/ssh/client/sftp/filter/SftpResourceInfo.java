package com.jn.agileway.ssh.client.sftp.filter;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.util.io.file.Filenames;

public class SftpResourceInfo {
    private String filepath;
    private String parent;
    private String name;
    private FileAttrs attrs;

    public SftpResourceInfo(String path, FileAttrs attrs) {
        this.filepath = path;
        this.attrs = attrs;
        this.name = Filenames.extractFilename(filepath);
        this.parent = filepath.substring(0, filepath.length() - name.length());
    }

    public String getPath() {
        return filepath;
    }

    public String getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public FileAttrs getAttrs() {
        return attrs;
    }

}
