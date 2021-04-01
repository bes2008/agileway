package com.jn.agileway.web.filter.waf.xss;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractRegexpXssHandler extends AbstractXssHandler {
    protected abstract List<Pattern> getIncludedPatterns();

    protected List<Pattern> getExcludedPatterns() {
        return Collects.unmodifiableArrayList();
    }

    @Override
    protected boolean isAttack(final String value) {
        if (Objs.isEmpty(value)) {
            return false;
        }

        List<Pattern> includedPatterns = getIncludedPatterns();

        if (Collects.anyMatch(includedPatterns, new Predicate<Pattern>() {
            @Override
            public boolean test(Pattern pattern) {
                return pattern.matcher(value).matches();
            }
        })) {
            if (Collects.anyMatch(getExcludedPatterns(), new Predicate<Pattern>() {
                @Override
                public boolean test(Pattern pattern) {
                    return pattern.matcher(value).matches();
                }
            })) {
                return false;
            }
            return true;
        }
        return false;
    }

}
