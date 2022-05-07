package com.jn.agileway.web.filter.waf;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.security.xss.JavaScriptXssHandler;
import com.jn.agileway.web.security.xss.XssFirewall;
import com.jn.agileway.web.rr.RR;
import com.jn.agileway.web.security.*;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;

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
    private XssFirewall xssFirewall;

    public XssFilter() {
    }

    public void setFirewall(XssFirewall xssFirewall) {
        this.xssFirewall = xssFirewall;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        WAFs.JAVA_SCRIPT_XSS_HANDLER.remove();
        if (Objs.isNotEmpty(xssFirewall) && xssFirewall.isEnabled() && request instanceof HttpServletRequest) {

            HttpServletResponse resp = (HttpServletResponse) response;
            WAFHttpServletResponseWrapper response2 = new WAFHttpServletResponseWrapper(resp);
            response2.setHttpOnlyCookies(xssFirewall.getConfig().getHttpOnlyCookies());
            response = response2;
            RR rr = getRR(request, response);
            rr.setResponse(response2);

            WAFStrategy strategy = xssFirewall.findStrategy(rr);
            if (Objs.isNotEmpty(strategy)) {
                JavaScriptXssHandler javaScriptXssHandler = (JavaScriptXssHandler) Collects.findFirst(strategy.getHandlers(), new Predicate<WAFHandler>() {
                    @Override
                    public boolean test(WAFHandler handler) {
                        return handler instanceof JavaScriptXssHandler;
                    }
                });
                if (javaScriptXssHandler != null) {
                    WAFs.JAVA_SCRIPT_XSS_HANDLER.set(javaScriptXssHandler);
                }
                request = new WAFHttpServletRequestWrapper(rr, strategy.getHandlers());
                rr.setRequest((HttpServletRequest) request);
                // ref: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-XSS-Protection
                ((HttpServletResponse) response).setHeader("X-XSS-Protection", "1;mode=block");
                // ref: https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
                // ref: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy
                // ref: http://www.ruanyifeng.com/blog/2016/09/csp.html
                // ref: https://cheatsheetseries.owasp.org/cheatsheets/Content_Security_Policy_Cheat_Sheet.html
                if (Objs.isNotEmpty(xssFirewall.getContentSecurityPolicy())) {
                    response2.setHeader("Content-Security-Policy", xssFirewall.getContentSecurityPolicy());
                }
            }
        }
        chain.doFilter(request, response);
    }
}
