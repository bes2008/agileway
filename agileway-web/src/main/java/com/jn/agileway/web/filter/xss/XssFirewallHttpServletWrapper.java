package com.jn.agileway.web.filter.xss;

import com.jn.agileway.web.servlet.RR;

import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

public class XssFirewallHttpServletWrapper extends HttpServletRequestWrapper {
    private List<XssHandler> xssHandlers;

    public XssFirewallHttpServletWrapper(RR holder, List<XssHandler> xssHandlers) {
        super(holder.getRequest());
        this.xssHandlers = xssHandlers;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] handledValues = new String[count];
        for (int i = 0; i < count; i++) {
            String handledValue = values[i];
            handledValues[i] = applyXssHandlers(handledValue);
        }
        return handledValues;
    }

    private String applyXssHandlers(String value) {
        if (value != null) {
            for (XssHandler xssHandler : xssHandlers) {
                value = xssHandler.apply(value);
            }
        }
        return value;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return applyXssHandlers(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return applyXssHandlers(value);
    }

}
