package com.jn.agileway.web.filter.xss;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.langx.util.Objs;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class XssFilter extends OncePerRequestFilter {
    private XssFirewall firewall;

    public void setFirewall(XssFirewall firewall) {
        this.firewall = firewall;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (Objs.isNotEmpty(firewall) && request instanceof HttpServletRequest) {

            List<XssHandler> handlers = firewall.getXssHandlers();
            request = new XssFirewallHttpServletWrapper((HttpServletRequest) request, handlers);
        }
        chain.doFilter(request, response);
    }
}
