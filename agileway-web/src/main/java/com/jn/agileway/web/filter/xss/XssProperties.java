package com.jn.agileway.web.filter.xss;

import java.util.List;
import java.util.Set;

public class XssProperties {
    /**
     * 是否启用 xss firewall
     */
    private boolean enabled = false;

    private List<String> includePaths;

    private List<String> excludePaths;


    /**
     * @see HtmlEventHandlerXssHandler
     */
    private Set<String> htmlEventHandlers = HtmlEventHandlerXssHandler.DEFAULT_ON_XXX_FUNCTION_NAMES;

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

    public List<String> getIncludePaths() {
        return includePaths;
    }

    public void setIncludePaths(List<String> includePaths) {
        this.includePaths = includePaths;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}
