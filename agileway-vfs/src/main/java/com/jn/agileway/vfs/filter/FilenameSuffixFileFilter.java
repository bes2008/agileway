package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.file.Filenames;

import java.util.List;

public class FilenameSuffixFileFilter extends FilenameEndsWithFileFilter {
    public FilenameSuffixFileFilter(boolean ignoreCase, String suffix) {
        this(ignoreCase, Collects.asList(suffix));
    }

    public FilenameSuffixFileFilter(boolean ignoreCase, String... suffixes) {
        this(ignoreCase, Collects.asList(suffixes));
    }

    public FilenameSuffixFileFilter(boolean ignoreCase, List<String> suffixes) {
        super(ignoreCase, suffixes);
    }

    @Override
    protected boolean doTest(String filename) {
        String suffix = Filenames.getSuffix(filename);
        return super.doTest(suffix);
    }
}
