package com.jn.agileway.web.filter.xss;

import com.jn.langx.util.Objs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class JavaScriptXssHandler extends AbstractXssHandler {
    private static final List<Pattern> FILTER_PATTERNS = Collections.unmodifiableList(Arrays.<Pattern>asList(
            // Avoid anything between script tags
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // Avoid javascript:... expressions
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // Remove any lonesome </script> tag
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid anything in a src='...' type of expression
            Pattern.compile("src[\r\n]*=[\r\n]*'(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid eval(...) expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid vbscript:... expressions
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)
    ));

    @Override
    protected boolean isAttack(String value) {
        if (Objs.isNotEmpty(value)) {
            for (Pattern pattern : FILTER_PATTERNS) {
                return pattern.matcher(value).matches();
            }
        }
        return false;
    }
}
