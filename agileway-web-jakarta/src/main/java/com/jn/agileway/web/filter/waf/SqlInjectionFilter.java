package com.jn.agileway.web.filter.waf;

import com.jn.agileway.http.rr.RR;
import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.security.WAF;
import com.jn.agileway.web.security.WAFHttpServletRequestWrapper;
import com.jn.agileway.web.security.WAFStrategy;
import com.jn.agileway.web.servlet.RRHolder;
import com.jn.langx.util.Objs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class SqlInjectionFilter extends OncePerRequestFilter {
    private WAF sqlWaf;

    public SqlInjectionFilter() {
    }

    public void setFirewall(WAF sqlFirewall) {
        this.sqlWaf = sqlFirewall;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (Objs.isNotEmpty(sqlWaf) && sqlWaf.isEnabled() && request instanceof HttpServletRequest) {
            RR rr = RRHolder.get();
            if (rr == null) {
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
                rr = RRHolder.get();
            }
            WAFStrategy strategy = sqlWaf.findStrategy(rr);
            if (Objs.isNotEmpty(strategy)) {
                request = new WAFHttpServletRequestWrapper(rr, strategy.getHandlers());
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
            }
        }
        chain.doFilter(request, response);
    }
}
