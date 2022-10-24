package com.jn.agileway.web.filter.waf;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.net.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class AllowedMethodsFilter extends OncePerRequestFilter {
    private List<HttpMethod> allowedMethods = Collects.newArrayList(
            HttpMethod.PUT,
            HttpMethod.POST,
            HttpMethod.GET,
            HttpMethod.DELETE
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    public void setAllowedMethods(List<HttpMethod> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();
        HttpMethod m = HttpMethod.valueOf(method);
        if (allowedMethods.contains(m)) {
            chain.doFilter(request, response);
        } else {

        }
    }
}
