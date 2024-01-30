package com.jn.agileway.web.security.xss;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Pipeline;

import java.util.List;
import java.util.Set;

public class XssProperties extends HttpRequestPredicateConfigItems {
    /**
     * 是否启用 xss firewall
     */
    private boolean enabled = false;

    private String contentSecurityPolicy = "default-src 'self' 'unsafe-hashes' 'unsafe-inline' data: blob: https:";

    private boolean htmlEventHandlersEnabled = true;
    /**
     * @see HtmlEventHandlerXssHandler
     */
    private String[] htmlEventHandlers = HtmlEventHandlerXssHandler.DEFAULT_ON_XXX_FUNCTION_NAMES.toArray(new String[0]);

    private boolean htmlTagsEnabled = true;

    /**
     * @see HtmlTagXssHandler
     */
    private String[] htmlTags = HtmlTagXssHandler.DEFAULT_TAGS.toArray(new String[0]);

    /**
     * @see JavaScriptXssHandler
     */
    private boolean javascriptEnabled = true;

    private String[] httpOnlyCookies;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getHtmlEventHandlers() {
        return Pipeline.of(htmlEventHandlers).asSet(true);
    }

    public void setHtmlEventHandlers(String[] htmlEventHandlers) {
        this.htmlEventHandlers = Arrs.copy(htmlEventHandlers);
    }

    public Set<String> getHtmlTags() {
        return Pipeline.of(htmlTags).asSet(true);
    }

    public void setHtmlTags(String[] htmlTags) {
        this.htmlTags = Arrs.copy(htmlTags);
    }

    public boolean isJavascriptEnabled() {
        return javascriptEnabled;
    }

    public void setJavascriptEnabled(boolean javascriptEnabled) {
        this.javascriptEnabled = javascriptEnabled;
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

    public void setHttpOnlyCookies(String[] httpOnlyCookies) {
        this.httpOnlyCookies = Arrs.copy(httpOnlyCookies);
    }

    public List<String> getHttpOnlyCookies() {
        return Pipeline.of(httpOnlyCookies).asList();
    }
}
