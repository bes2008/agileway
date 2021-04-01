package com.jn.agileway.web.filter.waf.xss;

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
 * https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html#xss-prevention-rules-summary
 */
public class XssFilter extends OncePerRequestFilter {
    private WAF xssFirewall;

    public XssFilter() {
    }

    public void setFirewall(WAF xssFirewall) {
        this.xssFirewall = xssFirewall;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (Objs.isNotEmpty(xssFirewall) && xssFirewall.isEnabled() && request instanceof HttpServletRequest) {

            RR rr = RRHolder.get();
            if (rr == null) {
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
                rr = RRHolder.get();
            }
            WAFStrategy strategy = xssFirewall.findStrategy(rr);
            if (Objs.isNotEmpty(strategy)) {
                request = new WAFHttpServletWrapper(rr, strategy.getHandlers());
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
                // ref: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-XSS-Protection
                ((HttpServletResponse) response).setHeader("X-XSS-Protection", "1;mode=block");
            }
        }
        chain.doFilter(request, response);
    }
}
