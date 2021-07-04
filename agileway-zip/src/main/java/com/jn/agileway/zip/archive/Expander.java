package com.jn.agileway.zip.archive;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.struct.Entry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * ArchiveInputStream API 有个缺点，就是因为被封装成了InputStream，所有就只能往前。
 * 如果想要先知道是否有指定的entry存在
 */
public class Expander implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(Expander.class);
    private ArchiveIterator iterator;
    @NonNull
    private InputStream inputStream;

    /**
     * 避免展开文件时，把文件属性丢失了。譬如说一个可执行文件，copy后，执行的权限丢失了
     */
    @NonNull
    private FileAttrsCopier fileAttrCopier = new FileAttrsCopier() {
        @Override
        public void accept(ArchiveEntry archiveEntry, File file) {
            // NOOP
        }
    };

    private boolean overwriteExistsFiles = false;

    public void setOverwriteExistsFiles(boolean overwriteExistsFiles) {
        this.overwriteExistsFiles = overwriteExistsFiles;
    }

    public void setFileAttrCopier(FileAttrsCopier fileAttrCopier) {
        this.fileAttrCopier = fileAttrCopier;
    }

    public Expander(String format, InputStream in) throws ArchiveException {
        iterator = new ArchiveIterator(format, in);
        this.inputStream = in;
    }

    public Expander(String format, String filepath) throws IOException, ArchiveException {
        this(format, new FileInputStream(filepath));
    }

    public Expander(String format, File file) throws IOException, ArchiveException {
        this(format, new FileInputStream(file));
    }


    public void setFilter(ArchiveEntryFilter filter) {
        iterator.setFilter(filter);
    }


    public void expandTo(File directory) throws IOException {
        Preconditions.checkNotNull(directory, "the directory is null");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {}", directory.getPath()));
            }
        }

        TreeSet<Entry<ArchiveEntry, File>> directorySet = new TreeSet<Entry<ArchiveEntry, File>>(new Comparator<Entry<ArchiveEntry, File>>() {
            @Override
            public int compare(Entry<ArchiveEntry, File> e1, Entry<ArchiveEntry, File> e2) {
                String entryName1 = e1.getKey().getName();
                String entryName2 = e2.getKey().getName();
                if (Strings.startsWith(entryName1, entryName2)) {
                    return -1;
                }
                if (Strings.startsWith(entryName2, entryName1)) {
                    return 1;
                }
                return entryName2.compareTo(entryName1);
            }
        });


        while (iterator.hasNext()) {
            ArchiveIterator.ArchiveEntryWrapper entryWrapper = iterator.next();
            ArchiveEntry entry = entryWrapper.getEntry();
            File f = new File(directory, entry.getName());
            if (f.getParentFile().exists() && !f.getParentFile().isDirectory()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {} to {}", entry.getName(), directory.getPath()));
            }
            if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) {
                throw new IOException(StringTemplates.formatWithPlaceholder("Can't expand {} to {}", entry.getName(), directory.getPath()));
            }
            logger.info("expand {}", entry.getName());

            if (Strings.endsWith(entry.getName(), "/")) {
                f.mkdirs();
                directorySet.add(new Entry<ArchiveEntry, File>(entry, f));
            } else {
                if (f.exists()) {
                    if (overwriteExistsFiles) {
                        f.delete();
                        f.createNewFile();
                    }
                } else {
                    f.createNewFile();
                }

                BufferedOutputStream bout = null;
                try {
                    bout = new BufferedOutputStream(new FileOutputStream(f));
                    IOs.copy(entryWrapper.getInputStream(), bout, 8192);
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    IOs.close(bout);
                }
            }
            fileAttrCopier.accept(entry, f);
            f.setLastModified(entry.getLastModifiedDate().getTime());
        }

        Collects.forEach(directorySet, new Consumer<Entry<ArchiveEntry, File>>() {
            @Override
            public void accept(Entry<ArchiveEntry, File> pair) {
                File directory = pair.getValue();
                ArchiveEntry entry = pair.getKey();
                directory.setLastModified(entry.getLastModifiedDate().getTime());
            }
        });
    }

    @Override
    public void close() throws IOException {
        IOs.close(this.inputStream);
    }
}
