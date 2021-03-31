package com.jn.agileway.web.filter.xss;

import com.jn.langx.util.collection.Collects;

import java.util.List;
import java.util.regex.Pattern;

public class HtmlTagXssHandler extends AbstractRegexpXssHandler {

    private static final List<Pattern> FILTER_PATTERNS = Collects.<Pattern>unmodifiableArrayList(Collects.asList(
            Pattern.compile("(<input(.*?)></input>|<input(.*)/>)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<span(.*?)</span>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<div(.*)</div>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<style>(.*?)</style>", Pattern.CASE_INSENSITIVE)
            )
    );

    @Override
    protected List<Pattern> getIncludedPatterns() {
        return FILTER_PATTERNS;
    }


}
