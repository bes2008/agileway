package com.jn.agileway.web.filter.accesslog;

import com.jn.agileway.web.servlet.Servlets;
import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class AccessLogFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);
    private WebAccessLogProperties config = new WebAccessLogProperties();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Initial web filter {} with init parameters: {}, custom config: {}", filterConfig.getFilterName(), Servlets.extractFilterInitParameters(filterConfig), JSONBuilderProvider.simplest().toJson(this.config));
        if (logger.isDebugEnabled()) {
            String level = filterConfig.getInitParameter("logLevel");
            if (Emptys.isNotEmpty(level)) {
                AccessLogLevel logLevel = Enums.ofName(AccessLogLevel.class, level);
                if (logLevel != null) {
                    this.config.setLevel(logLevel);
                }
            }
        }
    }

    public void setConfig(WebAccessLogProperties properties) {
        this.config = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 记录请求
        if (request instanceof HttpServletRequest) {
            HttpServletRequest r = (HttpServletRequest) request;
            if (logger.isDebugEnabled() && config.getLevel() != AccessLogLevel.NONE) {
                StringBuilder b = null;

                switch (config.getLevel()) {
                    case BASIC:
                        b = requestBasic(r);
                        break;
                    case HEADERS:
                        b = requestHeaders(r);
                        break;
                    case BODY:
                        b = requestBody(r);
                        break;
                    case NONE:
                        break;
                }

                if (b != null) {
                    logger.debug("====http request===>\n{}", b.toString());
                }
            }
        }

        chain.doFilter(request, response);

        // 记录 response
        if (response instanceof HttpServletResponse) {
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpServletRequest req = (HttpServletRequest) request;
            if (logger.isDebugEnabled() && config.getLevel() != AccessLogLevel.NONE) {
                StringBuilder b = null;

                switch (config.getLevel()) {
                    case BASIC:
                        b = responseBasic(req, resp);
                        break;
                    case HEADERS:
                    case BODY:
                        b = responseHeaders(req, resp);
                        break;
                    case NONE:
                        break;
                }
                logger.debug("<====http response=== \n{}", b.toString());
            }
        }
    }


    private StringBuilder requestBasic(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(256);
        builder.append(request.getProtocol()).append(" ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\n");
        if (Emptys.isNotEmpty(request.getQueryString())) {
            builder.append("query string: ").append(request.getQueryString()).append("\n");
        }
        builder.append("client host: ").append(request.getRemoteHost()).append("\n");


        return builder;
    }


    private StringBuilder requestHeaders(final HttpServletRequest request) {
        final StringBuilder builder = requestBasic(request);
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames.hasMoreElements()) {
            builder.append("headers: ").append("\n");
            Collects.forEach(headerNames, new Consumer<String>() {
                @Override
                public void accept(String headerName) {
                    Enumeration<String> valuesEnum = request.getHeaders(headerName);
                    if (valuesEnum.hasMoreElements()) {
                        builder.append("\t").append(headerName).append(" = ");
                        List<String> values = Collects.<String>asList(Collects.<String>asIterable(valuesEnum));
                        if (Emptys.getLength(values) > 1) {
                            Collects.forEach(values, new Consumer<String>() {
                                @Override
                                public void accept(String value) {
                                    builder.append("\t\t").append(value).append("\n");
                                }
                            });
                        } else {
                            builder.append(values.get(0)).append("\n");
                        }
                    }
                }
            });
        }
        return builder;
    }

    private StringBuilder requestBody(final HttpServletRequest request) {
        final StringBuilder builder = requestHeaders(request);
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            builder.append("parameters:\n");

            Collects.forEach(parameterNames, new Consumer<String>() {
                @Override
                public void accept(String parameterName) {
                    String[] values = request.getParameterValues(parameterName);

                    if (Emptys.isNotEmpty(values)) {
                        builder.append("\t").append(parameterName).append(" = ");
                        if (Emptys.getLength(values) > 1) {
                            Collects.forEach(values, new Consumer<String>() {
                                @Override
                                public void accept(String value) {
                                    builder.append("\t\t").append(value).append("\n");
                                }
                            });
                        } else {
                            builder.append(values[0]).append("\n");
                        }
                    }
                }
            });
        }
        return builder;
    }

    private StringBuilder responseBasic(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder(256);
        builder.append(request.getProtocol()).append(" ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\n");
        builder.append("StatusCode: ").append(response.getStatus()).append("\n");
        long contentLength = Servlets.getContentLength(response);
        builder.append("Content-Length: ").append(contentLength).append("\n");
        return builder;
    }

    private StringBuilder responseHeaders(HttpServletRequest request, final HttpServletResponse response) {
        final StringBuilder builder = responseBasic(request, response);
        Collection<String> headerNames = response.getHeaderNames();

        if (Emptys.isNotEmpty(headerNames)) {
            builder.append("headers: ").append("\n");
            Collects.forEach(headerNames, new Consumer<String>() {
                @Override
                public void accept(String headerName) {
                    Collection<String> values = response.getHeaders(headerName);
                    if (Emptys.isNotEmpty(values)) {
                        builder.append("\t").append(headerName).append(" = ");
                        if (Emptys.getLength(values) > 1) {
                            Collects.forEach(values, new Consumer<String>() {
                                @Override
                                public void accept(String value) {
                                    builder.append("\t\t").append(value).append("\n");
                                }
                            });
                        } else {
                            builder.append(values.iterator().next()).append("\n");
                        }
                    }
                }
            });
        }
        return builder;
    }

    @Override
    public void destroy() {
        logger.info("Access log filter destroyed");
    }
}
