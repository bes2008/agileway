package com.jn.agileway.web.filter;

import com.jn.agileway.web.request.handler.HttpRequestHandler;
import com.jn.agileway.web.servlet.RR;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class HttpRequestHandlerFilter extends OncePerRequestFilter {
    private HttpRequestHandler handler;

    public void setHandler(HttpRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        final RR rr = getRR(request, response);
        if (rr != null) {
            request = rr.getRequest();
            response = rr.getResponse();
            handler.handle(rr);
        }
        chain.doFilter(request, response);
    }
}
