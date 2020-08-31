package com.jn.agileway.web.filter.rr;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RRFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RRFilter.class);

    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        logger.info("Initial Base Web Filter (RRFilter) with config : {}", filterConfig);
    }

    public void doFilterInternal(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        try {
            if (request instanceof HttpServletRequest) {
                RRHolder.set((HttpServletRequest) request, (HttpServletResponse) response);
            }
            chain.doFilter(request, response);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            Throwables.throwAsRuntimeException(t);
        } finally {
            RRHolder.remove();
        }
    }
}
