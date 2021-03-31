package com.jn.agileway.web.filter.xss;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class HtmlTagXssHandler extends AbstractRegexpXssHandler {

    static final Set<String> DEFAULT_TAGS = Collects.unmodifiableSet(
            "style", "script"
    );

    private List<Pattern> includePatterns;

    @Override
    protected List<Pattern> getIncludedPatterns() {
        return includePatterns;
    }

    public void setIncludeTags(List<String> tags) {
        this.includePatterns = toPatterns(tags);
    }

    private static List<Pattern> toPatterns(Collection<String> tags) {
        return Pipeline.of(tags).clearNulls().distinct().map(new Function<String, Pattern>() {
            @Override
            public Pattern apply(String tag) {
                return Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            }
        }).asList();
    }

    @Override
    protected void doInit() throws InitializationException {
        if (Objs.isEmpty(includePatterns)) {
            includePatterns = toPatterns(DEFAULT_TAGS);
        }
    }
}
