package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.filter.rr.RRHolder;
import com.jn.agileway.web.filter.waf.WAFHandler;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.Objs;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class XssFilter extends OncePerRequestFilter {
    private XssFirewall firewall;

    public XssFilter(){
    }

    public void setFirewall(XssFirewall firewall) {
        this.firewall = firewall;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (Objs.isNotEmpty(firewall) && firewall.isEnabled() && request instanceof HttpServletRequest) {

            List<WAFHandler> handlers = firewall.getXssHandlers();
            RR rr = RRHolder.get();
            if (rr == null) {
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
                rr = RRHolder.get();
            }
            request = new XssFirewallHttpServletWrapper(rr, handlers);
            RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
            // ref: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-XSS-Protection
            ((HttpServletResponse) response).setHeader("X-XSS-Protection","1;mode=block");
        }
        chain.doFilter(request, response);
    }
}
