package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.waf.WAF;
import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.prediates.HttpRequestPredicateFactoryRegistry;
import com.jn.agileway.web.prediates.PathMatchPredicate;
import com.jn.agileway.web.prediates.PathMatchPredicateFactory;
import com.jn.langx.factory.Factory;
import com.jn.langx.util.Objs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XssFirewallFactory implements Factory<XssProperties, WAF> {
    @Override
    public WAF get(XssProperties xssProps) {
        WAF firewall = new WAF();
        firewall.setEnabled(xssProps.isEnabled());

        // path match predicate
        PathMatchPredicateFactory factory = (PathMatchPredicateFactory) HttpRequestPredicateFactoryRegistry.getInstance().get("path");
        if (factory == null) {
            factory = new PathMatchPredicateFactory();
        }
        Map<String, List<String>> pathConfig = new HashMap<String, List<String>>();
        if (Objs.isNotEmpty(xssProps.getIncludePaths())) {
            pathConfig.put(PathMatchPredicateFactory.INCLUDE, xssProps.getIncludePaths());
        }
        if (Objs.isNotEmpty(xssProps.getExcludePaths())) {
            pathConfig.put(PathMatchPredicateFactory.EXCLUDE, xssProps.getExcludePaths());
        }
        PathMatchPredicate pathMatchPredicate = (PathMatchPredicate) factory.get(pathConfig);

        WAFStrategy xssStrategy = new WAFStrategy();
        xssStrategy.addPredicate(pathMatchPredicate);


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
