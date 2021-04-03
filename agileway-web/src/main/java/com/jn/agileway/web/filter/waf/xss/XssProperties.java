package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

import java.util.Set;

public class XssProperties {
    /**
     * 是否启用 xss firewall
     */
    private boolean enabled = false;

    private HttpRequestPredicateConfigItems predicates;

    private String contentSecurityPolicy = "default-src 'self'";

    private boolean htmlEventHandlersEnabled = true;
    /**
     * @see HtmlEventHandlerXssHandler
     */
    private Set<String> htmlEventHandlers = HtmlEventHandlerXssHandler.DEFAULT_ON_XXX_FUNCTION_NAMES;

    private boolean htmlTagsEnabled = true;

    /**
     * @see HtmlTagXssHandler
     */
    private Set<String> htmlTags = HtmlTagXssHandler.DEFAULT_TAGS;

    /**
     * @see JavaScriptXssHandler
     */
    private boolean javascriptEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getHtmlEventHandlers() {
        return htmlEventHandlers;
    }

    public void setHtmlEventHandlers(Set<String> htmlEventHandlers) {
        this.htmlEventHandlers = htmlEventHandlers;
    }

    public Set<String> getHtmlTags() {
        return htmlTags;
    }

    public void setHtmlTags(Set<String> htmlTags) {
        this.htmlTags = htmlTags;
    }

    public boolean isJavascriptEnabled() {
        return javascriptEnabled;
    }

    public void setJavascriptEnabled(boolean javascriptEnabled) {
        this.javascriptEnabled = javascriptEnabled;
    }

    public HttpRequestPredicateConfigItems getPredicates() {
        return predicates;
    }

    public void setPredicates(HttpRequestPredicateConfigItems predicates) {
        this.predicates = predicates;
    }

    public boolean isHtmlEventHandlersEnabled() {
        return htmlEventHandlersEnabled;
    }

    public void setHtmlEventHandlersEnabled(boolean htmlEventHandlersEnabled) {
        this.htmlEventHandlersEnabled = htmlEventHandlersEnabled;
    }

    public boolean isHtmlTagsEnabled() {
        return htmlTagsEnabled;
    }

    public void setHtmlTagsEnabled(boolean htmlTagsEnabled) {
        this.htmlTagsEnabled = htmlTagsEnabled;
    }

    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }
}
