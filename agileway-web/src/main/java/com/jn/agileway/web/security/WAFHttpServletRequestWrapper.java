package com.jn.agileway.web.security;

import com.jn.agileway.http.rr.RR;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

public class WAFHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private List<WAFHandler> wafHandlers;

    public WAFHttpServletRequestWrapper(RR holder, List<WAFHandler> xssHandlers) {
        super((HttpServletRequest) holder.getRequest().getContainerRequest());
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
            handledValues[i] = applyWAFHandlers(handledValue, false);
        }
        return handledValues;
    }

    private String applyWAFHandlers(String value, boolean isRequestHeader) {
        if (value != null) {
            for (WAFHandler wafHandler : wafHandlers) {
                if (!isRequestHeader || wafHandler.requestHeaderAware()) {
                    value = wafHandler.apply(value);
                }
            }
        }
        return value;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return applyWAFHandlers(value, false);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return applyWAFHandlers(value, true);
    }

}
