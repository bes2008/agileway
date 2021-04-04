package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.agileway.web.prediate.HttpRequestPredicates;
import com.jn.langx.annotation.Name;
import com.jn.langx.util.Strings;

import java.util.regex.Pattern;

/**
 * 需要2个参数，一个是header名，另外一个header值，该值可以是一个正则表达式。
 * 当此断言匹配了请求的header名和值时，断言通过，进入到router的规则中去
 */
@Name("Header")
public class HeaderMatchPredicateFactory extends HttpRequestPredicateFactory {
    public HeaderMatchPredicateFactory() {
        setName(HttpRequestPredicates.PREDICATE_KEY_HEADER);
    }

    @Override
    public HttpRequestPredicate get(String configuration) {
        HeaderMatchPredicate predicate = null;
        if (Strings.isNotBlank(configuration)) {
            String[] segments = Strings.split(configuration, ", ");
            if (segments.length > 0) {
                predicate = new HeaderMatchPredicate();
                predicate.setHeader(segments[0]);

                if (segments.length > 1) {
                    predicate.setValuePattern(Pattern.compile(segments[1]));
                }
            }
        }
        return predicate;
    }
}
