package com.jn.agileway.zip.archive;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ArchiveIterator implements Iterator<ArchiveIterator.ArchiveEntryWrapper> {
    private static Logger logger = LoggerFactory.getLogger(ArchiveIterator.class);

    @NonNull
    private ArchiveInputStream archiveInputStream;
    private ArchiveEntry next;
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

    public ArchiveIterator(String format, InputStream in) {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        try {
            this.archiveInputStream = new ArchiveStreamFactory(Charsets.UTF_8.name()).createArchiveInputStream(format, in);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public void setFilter(ArchiveEntryFilter filter) {
        if (filter != null) {
            this.filter = filter;
        }
    }

    /**
     * @return 返回 找到的可读的 entry, 如果返回 null, 则代表没有了
     */
    private ArchiveEntry findNextReadableEntry() {
        ArchiveEntry entry = null;
        while (entry == null) {
            try {
                entry = archiveInputStream.getNextEntry();
                if (entry == null) {
                    return null;
                }
                if (!archiveInputStream.canReadEntryData(entry)) {
                    String entryName = entry.getName();
                    entry = null;
                    logger.warn("An unreadable entry found: {}", entryName);
                }
                if (!filter.accept(entry)) {
                    entry = null;
                }
            } catch (IOException ex) {
                if (ex instanceof EOFException) {
                    return null;
                }
                logger.error(ex.getMessage(), ex);
            }
        }
        return entry;
    }

    @Override
    public boolean hasNext() {
        if (next == null) {
            next = findNextReadableEntry();
        }
        boolean hasNext = next != null;
        if (!hasNext) {
            IOs.close(archiveInputStream);
        }
        return hasNext;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArchiveEntryWrapper next() {
        ArchiveEntryWrapper wrapper = new ArchiveEntryWrapper(next, archiveInputStream);
        next = null;
        return wrapper;
    }

    public static class ArchiveEntryWrapper {
        private ArchiveEntry entry;
        private InputStream inputStream;

        public ArchiveEntryWrapper(ArchiveEntry entry, InputStream inputStream) {
            this.entry = entry;
            this.inputStream = inputStream;
        }

        public ArchiveEntry getEntry() {
            return entry;
        }

        public void setEntry(ArchiveEntry entry) {
            this.entry = entry;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }

}
