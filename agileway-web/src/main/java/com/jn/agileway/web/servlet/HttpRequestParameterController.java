package com.jn.agileway.web.servlet;


import java.util.List;

public class HttpRequestParameterController {

    protected String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Get http parameter NULL");
        }
        return Servlets.getParameter(RRHolder.getRequest(), name);
    }

    protected String getStringParameter(final String name) {
        return this.getParameter(name);
    }

    protected List<String> getParameters(final String name) {
        return Servlets.getParameters(RRHolder.getRequest(), name);
    }

    protected List<String> getStringParameters(final String name) {
        return this.getParameters(name);
    }

    protected Integer getIntParameter(final String name) {
        return Servlets.getIntParameter(RRHolder.getRequest(), name, null);
    }

    protected List<Integer> getIntParameters(final String name) {
        return Servlets.getIntParameters(RRHolder.getRequest(), name, null);
    }

    protected Long getLongParameter(final String name) {
        return Servlets.getLongParameter(RRHolder.getRequest(), name, null);
    }

    protected List<Long> getLongParameters(final String name) {
        return Servlets.getLongParameters(RRHolder.getRequest(), name, null);
    }

    protected Float getFloatParameter(final String name) {
        return Servlets.getFloatParameter(RRHolder.getRequest(), name, null);
    }

    protected List<Float> getFloatParameters(final String name) {
        return Servlets.getFloatParameters(RRHolder.getRequest(), name, null);
    }

    protected Double getDoubleParameter(final String name) {
        return Servlets.getDoubleParameter(RRHolder.getRequest(), name, null);
    }

    protected List<Double> getDoubleParameters(final String name) {
        return Servlets.getDoubleParameters(RRHolder.getRequest(), name, null);
    }

    protected final static Boolean isTrue(final String value) {
        return Servlets.isTrue(value);
    }

    protected Boolean getBooleanParameter(final String name) {
        return Servlets.getBooleanParameter(RRHolder.getRequest(), name, null);
    }

    protected Boolean getBooleanParameter(final String name, final boolean defaultValue) {
        return Servlets.getBooleanParameter(RRHolder.getRequest(), name, defaultValue);
    }

    protected List<Boolean> getBooleanParameters(final String name) {
        return Servlets.getBooleanParameters(RRHolder.getRequest(), name, null);
    }
}

