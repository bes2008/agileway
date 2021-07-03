package com.jn.agileway.zip.archive;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.IOs;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * ArchiveInputStream API 有个缺点，就是因为被封装成了InputStream，所有就只能往前。
 * 如果想要先知道是否有指定的entry存在
 */
public class Expander implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(Expander.class);
    @NonNull
    private ArchiveInputStream archiveInputStream;

    /**
     * 用于筛选出期望展开的文件
     */
    @NonNull
    private ArchiveEntryFilter filter = new ArchiveEntryFilter() {
        @Override
        public boolean accept(ArchiveEntry archiveEntry) {
            return true;
        }
    };

    /**
     * 避免展开文件时，把文件属性丢失了。譬如说一个可执行文件，copy后，执行的权限丢失了
     */
    @NonNull
    private FileAttrCopier fileAttrCopier = new FileAttrCopier() {
        @Override
        public void accept(ArchiveEntry archiveEntry, File file) {
            // NOOP
        }
    };

    public void setFileAttrCopier(FileAttrCopier fileAttrCopier) {
        this.fileAttrCopier = fileAttrCopier;
    }

    public Expander(String format, InputStream in) throws ArchiveException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        this.archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(format, in);
    }

    public Expander(String format, String filepath) throws IOException, ArchiveException {
        this(format, new FileInputStream(filepath));
    }

    public Expander(String format, File file) throws IOException, ArchiveException {
        this(format, new FileInputStream(file));
    }


    public void setFilter(ArchiveEntryFilter filter) {
        if (filter != null) {
            this.filter = filter;
        }
    }

    public void expandTo(File directory) throws IOException {
        Preconditions.checkNotNull(directory, "the directory is null");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {}", directory.getPath()));
            }
        }
        ArchiveEntry entry = findNextReadableEntry();
        while (entry != null) {

            File f = new File(directory, entry.getName());
            if (f.getParentFile().exists() && !f.getParentFile().isDirectory()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {} to {}", entry.getName(), directory.getPath()));
            }
            if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {} to {}", entry.getName(), directory.getPath()));
            }

            BufferedOutputStream bout = null;
            try {
                bout = new BufferedOutputStream(new FileOutputStream(f));
                IOs.copy(archiveInputStream, bout, 8192);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                IOs.close(bout);
            }

            fileAttrCopier.accept(entry, f);
            entry = findNextReadableEntry();
        }


    }

    private ArchiveEntry findNextReadableEntry() {
        ArchiveEntry entry = null;
        while (entry == null) {
            try {
                entry = archiveInputStream.getNextEntry();
                if (entry == null || !archiveInputStream.canReadEntryData(entry) || !filter.accept(entry)) {
                    entry = null;
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return entry;
    }

    @Override
    public void close() throws IOException {
        IOs.close(this.archiveInputStream);
    }
}
