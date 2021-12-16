package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import org.apache.commons.vfs2.FileObject;

import java.util.List;

public class AllMatchFileFilter implements FileObjectFilter {
    private List<Predicate<FileObject>> predicates;

    public AllMatchFileFilter(FileObjectFilter... predicates){
        this(Collects.asList(predicates));
    }

    public AllMatchFileFilter(List<FileObjectFilter> predicates) {
        List ps = predicates;
        this.predicates = ps;
    }

    @Override
    public boolean test(FileObject fileObject) {
        return Functions.allPredicate(predicates).test(fileObject);
    }
}
