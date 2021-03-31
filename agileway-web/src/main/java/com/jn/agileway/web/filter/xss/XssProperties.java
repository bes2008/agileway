package com.jn.agileway.web.filter.xss;

import java.util.Set;

public class XssProperties {
    /**
     * 是否启用 xss firewall
     */
    private boolean enabled = true;

    private Set<String> htmlEventHandlers = HtmlEventHandlerXssHandler.DEFAULT_ON_XXX_FUNCTION_NAMES;

    private Set<String> htmlTags = HtmlTagXssHandler.DEFAULT_TAGS;


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
}
