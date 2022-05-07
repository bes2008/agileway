package com.jn.agileway.web.filter.rr;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.servlet.HttpServletRequestStreamWrapper;
import com.jn.agileway.web.servlet.RRHolder;
import com.jn.langx.util.BooleanEvaluator;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RRFilter extends OncePerRequestFilter {
    private static final Logger logger = Loggers.getLogger(RRFilter.class);
    private boolean streamWrapperEnabled = false;
    private String encoding = "UTF-8";

    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        String streamWrapperEnabled = filterConfig.getInitParameter("streamWrapperEnabled");
        boolean enabled = BooleanEvaluator.createTrueEvaluator(false, true, new Object[]{"true"}).evalTrue(streamWrapperEnabled);
        this.streamWrapperEnabled = enabled;

        String encoding = filterConfig.getInitParameter("encoding");
        if (Emptys.isNotEmpty(encoding)) {
            this.encoding = encoding;
        }

        logger.info("Initial Base Web Filter (RRFilter) with config : {}", filterConfig);

    }

    public void doFilterInternal(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        try {
            ServletRequest request1 = request;
            request.setCharacterEncoding(encoding);
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = (HttpServletRequest) request;
                String contentType = req.getContentType();
                boolean isMultipartRequest = false;
                if (Emptys.isNotEmpty(contentType) && contentType.contains("multipart")) {
                    isMultipartRequest = true;
                }
                if (streamWrapperEnabled && !isMultipartRequest) {
                    req = new HttpServletRequestStreamWrapper((HttpServletRequest) request);
                }
                RRHolder.set(req, (HttpServletResponse) response);
                request1 = req;
            }
            chain.doFilter(request1, response);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            Throwables.throwAsRuntimeException(t);
        } finally {
            RRHolder.remove();
        }
    }
}
