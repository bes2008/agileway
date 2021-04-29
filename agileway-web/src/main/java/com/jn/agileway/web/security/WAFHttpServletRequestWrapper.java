package com.jn.agileway.web.security;

import com.jn.agileway.web.servlet.RR;

import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

public class WAFHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private List<WAFHandler> wafHandlers;

    public WAFHttpServletRequestWrapper(RR holder, List<WAFHandler> xssHandlers) {
        super(holder.getRequest());
        this.wafHandlers = xssHandlers;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int length = values.length;
        String[] handledValues = new String[length];
        for (int i = 0; i < length; i++) {
            String handledValue = values[i];
            handledValues[i] = applyXssHandlers(handledValue);
        }
        return handledValues;
    }

    private String applyXssHandlers(String value) {
        if (value != null) {
            for (WAFHandler xssHandler : wafHandlers) {
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
