package com.jn.agileway.zip.archive;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.factory.Factory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.FileFilter;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Archiver implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(Archiver.class);

    @NonNull
    private ArchiveOutputStream archiveOutputStream;

    @NonNull
    private ArchiveEntryCustomizer archiveEntryCustomizer = new ArchiveEntryCustomizer() {
        @Override
        public void customize(ArchiveEntry entry) {

        }
    };

    @NonNull
    private Factory<File, String> entryNameFactory = new Factory<File, String>() {
        @Override
        public String get(File file) {
            return file.getName();
        }
    };

    // 用于对文件、目录进行过滤
    @Nullable
    private FileFilter fileFilter;

    private boolean ignoreEmptyDirectory = true;

    public Archiver(@NonNull String format, @NonNull OutputStream out, @Nullable ArchiveOutputStreamCustomizer customizer) throws ArchiveException {
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        archiveOutputStream = new ArchiveStreamFactory(Charsets.UTF_8.name()).createArchiveOutputStream(format, out);
        if(customizer!=null) {
            customizer.customize(archiveOutputStream);
        }
    }

    public Archiver(@NonNull String format, @NonNull String targetFilepath, @Nullable ArchiveOutputStreamCustomizer customizer) throws IOException, ArchiveException {
        this(format, new FileOutputStream(targetFilepath),customizer);
    }

    public Archiver(@NonNull String format, @NonNull File target, @Nullable ArchiveOutputStreamCustomizer customizer) throws IOException, ArchiveException {
        this(format, new FileOutputStream(target), customizer);
    }

    public boolean isIgnoreEmptyDirectory() {
        return ignoreEmptyDirectory;
    }

    public void setIgnoreEmptyDirectory(boolean ignoreEmptyDirectory) {
        this.ignoreEmptyDirectory = ignoreEmptyDirectory;
    }

    public void setEntryNameFactory(Factory<File, String> entryNameFactory) {
        if (entryNameFactory != null) {
            this.entryNameFactory = entryNameFactory;
        }
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public void setArchiveEntryCustomizer(ArchiveEntryCustomizer archiveEntryCustomizer) {
        if (archiveEntryCustomizer != null) {
            this.archiveEntryCustomizer = archiveEntryCustomizer;
        }
    }

    /**
     * 只用于添加文件
     *
     * @param file
     */
    public void addFile(File file) throws IOException {
        addFile(file, null);
    }

    /**
     * 只用于添加文件
     *
     * @param file            要添加的文件
     * @param entryNamePrefix 要添加到archive中哪个前缀下。可以为"" ，也可以为"xxxx/"
     */
    public void addFile(File file, String entryNamePrefix) throws IOException {
        if (file.isDirectory()) {
            addDirectory(file, entryNamePrefix);
        } else if (file.isFile()) {
            if (fileFilter != null) {
                if (!fileFilter.accept(file)) {
                    return;
                }
            }
            addArchiveEntry(file, entryNamePrefix);
        }

    }


    public void addDirectory(@NonNull File directory) throws IOException {
        addDirectory(directory, null);
    }

    public void addDirectory(@NonNull File directory, @Nullable String entryNamePrefix) throws IOException {
        if (directory.isFile()) {
            addFile(directory, entryNamePrefix);
        } else if (directory.isDirectory()) {
            if (fileFilter != null) {
                if (!fileFilter.accept(directory)) {
                    return;
                }
            }

            File[] children = (fileFilter != null && ignoreEmptyDirectory) ? directory.listFiles((java.io.FileFilter) fileFilter) : directory.listFiles();

            if (ignoreEmptyDirectory && Objs.isEmpty(children)) {
                // 忽略空目录
                return;
            }

            String entryName = addArchiveEntry(directory, entryNamePrefix);

            if (Objs.isNotEmpty(children)) {
                for (File file : children) {
                    addFile(file, entryName);
                }
            }
        }
    }

    private String addArchiveEntry(File file, String entryNamePrefix) throws IOException {
        String entryName = getEntryName(entryNamePrefix, file);

        // 创建 entry
        ArchiveEntry entry = archiveOutputStream.createArchiveEntry(file, entryName);
        archiveEntryCustomizer.customize(entry);

        // 添加 entry
        archiveOutputStream.putArchiveEntry(entry);
        if (file.isFile()) {
            // 写入数据
            InputStream fileInput = null;
            try {
                fileInput = new BufferedInputStream(new FileInputStream(file));
                long writeLength = IOs.copy(fileInput, archiveOutputStream, 8192);
                Preconditions.checkTrue(file.length()==writeLength);
            } catch (Throwable ex) {
                logger.error(ex.getMessage(),ex);
            }finally {
                IOs.close(fileInput);
            }
        }
        archiveOutputStream.closeArchiveEntry();
        return entryName;
    }

    private String getEntryName(String entryNamePrefix, File file) {
        entryNamePrefix = Strings.getEmptyIfBlank(Strings.trim(entryNamePrefix));
        entryNamePrefix = Objs.useValueIfMatch(entryNamePrefix, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return Strings.isEmpty(s) || "/".equals(s);
            }
        }, "");

        String entryName = Preconditions.checkNotEmpty(entryNameFactory.get(file));
        entryName = entryNamePrefix + entryName;
        if (file.isDirectory()) {
            entryName = entryName + "/";
        }
        return entryName;
    }


    @Override
    public void close() throws IOException {
        if (archiveOutputStream != null) {
            archiveOutputStream.finish();
            IOs.close(archiveOutputStream);
            archiveOutputStream = null;
        }
    }

}
