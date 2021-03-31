package com.jn.agileway.web.filter.xss;

import com.jn.agileway.web.prediates.HttpRequestPredicateFactoryRegistry;
import com.jn.agileway.web.prediates.PathMatchPredicate;
import com.jn.agileway.web.prediates.PathMatchPredicateFactory;
import com.jn.langx.factory.Factory;
import com.jn.langx.util.Objs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XssFirewallFactory implements Factory<XssProperties, XssFirewall> {
    @Override
    public XssFirewall get(XssProperties xssProps) {
        XssFirewall firewall = new XssFirewall();
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
        firewall.addPredicate(pathMatchPredicate);


        // html event handlers
        if (xssProps.isJavascriptEnabled()) {
            firewall.add(new JavaScriptXssHandler());
        }
        HtmlEventHandlerXssHandler eventHandlerXssHandler = new HtmlEventHandlerXssHandler();
        eventHandlerXssHandler.setFunctionNames(xssProps.getHtmlEventHandlers());
        firewall.add(eventHandlerXssHandler);

        // html tags
        HtmlTagXssHandler tagXssHandler = new HtmlTagXssHandler();
        tagXssHandler.setIncludeTags(xssProps.getHtmlTags());
        firewall.add(tagXssHandler);


        return firewall;
    }
}
