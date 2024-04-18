package com.jn.agileway.zip;

import com.jn.agileway.zip.archive.ArchiveIterator;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.logging.Loggers;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CompressUtils {
    private CompressUtils(){}
    private static final Logger logger = Loggers.getLogger(CompressUtils.class);

    public static ArchiveIterator iterator(File file){
        if (file==null || !file.exists() || !file.canRead()){
            return null;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String filepath = file.getPath();
            String format = CompressFormats.getFormat(filepath);
            return new ArchiveIterator(format, in);
        }catch (IOException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    public static List<ArchiveEntry> listEntries(File file){
        return Pipeline.<ArchiveIterator.ArchiveEntryWrapper>of(iterator(file)).map(new Function<ArchiveIterator.ArchiveEntryWrapper, ArchiveEntry>() {
            @Override
            public ArchiveEntry apply(ArchiveIterator.ArchiveEntryWrapper entryWrapper) {
                return entryWrapper.getEntry();
            }
        }).asList();
    }


}
