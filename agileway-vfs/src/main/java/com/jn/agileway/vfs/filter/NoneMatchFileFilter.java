package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import org.apache.commons.vfs2.FileObject;

import java.util.List;

public class NoneMatchFileFilter implements FileObjectFilter {
    private List<Predicate<FileObject>> predicates;

    public NoneMatchFileFilter(FileObjectFilter... predicates) {
        this(Collects.asList(predicates));
    }

    public NoneMatchFileFilter(List<FileObjectFilter> predicates) {
        List ps = predicates;
        this.predicates = ps;
    }

    @Override
    public boolean test(FileObject fileObject) {
        return Functions.nonePredicate(predicates).test(fileObject);
    }
}
