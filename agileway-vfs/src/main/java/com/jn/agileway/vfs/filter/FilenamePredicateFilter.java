package com.jn.agileway.vfs.filter;

import com.jn.langx.util.function.Predicate;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;

public class FilenamePredicateFilter  implements FileObjectFilter{
    private Predicate<String> predicate;

    public FilenamePredicateFilter(Predicate<String> predicate){
        this.predicate = predicate;
    }

    @Override
    public final boolean test(FileObject fileObject) {
        FileName fileName = fileObject.getName();
        String name = fileName.getBaseName();
        return  doTest(name);
    }

    protected boolean doTest(String filename){
        return predicate.test(filename);
    }
}
