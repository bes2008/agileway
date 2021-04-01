package com.jn.agileway.web.filter.waf.xcontenttype;

import com.jn.agileway.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XContentTypeOptionsFilter extends OncePerRequestFilter {
    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (enabled) {
            if (request instanceof HttpServletRequest) {
                ((HttpServletResponse) response).setHeader("X-Content-Type-Options", "nosniff");
            }
        }
        doFilter(request, response, chain);
    }
}
