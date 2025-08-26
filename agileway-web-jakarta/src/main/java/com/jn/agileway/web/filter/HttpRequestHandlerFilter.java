package com.jn.agileway.web.filter;

import com.jn.agileway.http.rr.RR;
import com.jn.agileway.web.request.handler.HttpRequestHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

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
            request = (ServletRequest) rr.getRequest().getContainerRequest();
            response = (ServletResponse) rr.getResponse().getContainerResponse();
            handler.handle(rr);
        }
        chain.doFilter(request, response);
    }
}
