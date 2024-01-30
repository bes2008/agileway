package com.jn.agileway.web.security.xss;

import com.jn.agileway.web.security.WAFStrategy;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroupFactory;
import com.jn.langx.Factory;

public class XssWafFactory implements Factory<XssProperties, XssFirewall> {
    @Override
    public XssFirewall get(XssProperties xssProps) {
        XssFirewall firewall = new XssFirewall();
        firewall.setName("XSS-Firewall");
        firewall.setConfig(xssProps);

        HttpRequestPredicateGroup predicates = new HttpRequestPredicateGroupFactory().get(xssProps);

        WAFStrategy xssStrategy = new WAFStrategy();
        xssStrategy.setPredicates(predicates);


        // html event handlers
        if (xssProps.isJavascriptEnabled()) {
            xssStrategy.add(new JavaScriptXssHandler());
        }

        if (xssProps.isHtmlEventHandlersEnabled()) {
            HtmlEventHandlerXssHandler eventHandlerXssHandler = new HtmlEventHandlerXssHandler();
            eventHandlerXssHandler.setFunctionNames(xssProps.getHtmlEventHandlers());
            xssStrategy.add(eventHandlerXssHandler);
        }

        // html tags
        if (xssProps.isHtmlTagsEnabled()) {
            HtmlTagXssHandler tagXssHandler = new HtmlTagXssHandler();
            tagXssHandler.setIncludeTags(xssProps.getHtmlTags());
            xssStrategy.add(tagXssHandler);
        }

        firewall.addStrategy(xssStrategy);

        return firewall;
    }
}
