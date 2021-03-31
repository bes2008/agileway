package com.jn.agileway.web.filter.xss;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class JavaScriptXssHandler extends AbstractRegexpXssHandler {
    private static final List<Pattern> FILTER_PATTERNS = Collections.unmodifiableList(Arrays.<Pattern>asList(
            // Avoid javascript:... expressions
            Pattern.compile(".*javascript:.*", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a src='...' type of expression
            Pattern.compile(".*src[\r\n]*=[\r\n]*'(.*?)'.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile(".*src[\r\n]*=[\r\n]*\"(.*?)\".*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid eval(...) expressions
            Pattern.compile(".*eval\\((.*?)\\).*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile(".*expression\\((.*?)\\).*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid vbscript:... expressions
            Pattern.compile(".*vbscript:.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*typescript:.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*actionscript:.*", Pattern.CASE_INSENSITIVE)
    ));

    @Override
    protected List<Pattern> getIncludedPatterns() {
        return FILTER_PATTERNS;
    }
}
