package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.filter.rr.RRHolder;
import com.jn.agileway.web.filter.waf.WAF;
import com.jn.agileway.web.filter.waf.WAFHttpServletWrapper;
import com.jn.agileway.web.filter.waf.WAFStrategy;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.Objs;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
 */
public class SqlInjectFilter extends OncePerRequestFilter {
    private WAF sqlWaf;

    public SqlInjectFilter() {
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
            if(Objs.isNotEmpty(strategy)) {
                request = new WAFHttpServletWrapper(rr, strategy.getHandlers());
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
            }
        }
        chain.doFilter(request, response);
    }
}
