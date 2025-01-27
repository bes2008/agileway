package com.jn.agileway.web.filter.waf.cors;


import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * <p>
 * A {@link Filter} that enable client-side cross-origin requests by
 * implementing W3C's CORS (<b>C</b>ross-<b>O</b>rigin <b>R</b>esource
 * <b>S</b>haring) specification for resources. Each {@link HttpServletRequest}
 * request is inspected as per specification, and appropriate response headers
 * are added to {@link HttpServletResponse}.
 * </p>
 *
 * <p>
 * By default, it also sets following request attributes, that help to
 * determine the nature of the request downstream.
 * </p>
 * <ul>
 * <li><b>cors.isCorsRequest:</b> Flag to determine if the request is a CORS
 * request. Set to <code>true</code> if a CORS request; <code>false</code>
 * otherwise.</li>
 * <li><b>cors.request.origin:</b> The Origin URL, i.e. the URL of the page from
 * where the request is originated.</li>
 * <li>
 * <b>cors.request.type:</b> Type of request. Possible values:
 * <ul>
 * <li>SIMPLE: A request which is not preceded by a pre-flight request.</li>
 * <li>ACTUAL: A request which is preceded by a pre-flight request.</li>
 * <li>PRE_FLIGHT: A pre-flight request.</li>
 * <li>NOT_CORS: A normal same-origin request.</li>
 * <li>INVALID_CORS: A cross-origin request which is invalid.</li>
 * </ul>
 * </li>
 * <li><b>cors.request.headers:</b> Request headers sent as
 * 'Access-Control-Request-Headers' header, for pre-flight request.</li>
 * </ul>
 *
 * @see <a href="http://www.w3.org/TR/cors/">CORS specification</a>
 * <p/>
 * <p>
 * migrate from tomcat-8.5.43
 * </p>
 * https://blog.csdn.net/mengmengdastyle/article/details/80939609
 * https://tomcat.apache.org/tomcat-9.0-doc/config/filter.html#CORS_Filter
 */
public class CorsFilter implements Filter {

    private final Logger log = Loggers.getLogger(this.getClass()); // must not be static

    private CorsProperties conf;

    public void setConf(CorsProperties conf) {
        this.conf = conf;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException("CORS doesn't support non-HTTP request or response");
        }

        // Safe to downcast at this point.
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Determines the CORS request type.
        CorsFilter.CORSRequestType requestType = checkRequestType(request);

        // Adds CORS specific attributes to request.
        if (this.conf.getRequest().isDecorate()) {
            CorsFilter.decorateCORSProperties(request, requestType);
        }
        switch (requestType) {
            case SIMPLE:
                // Handles a Simple CORS request.
                this.handleSimpleCORS(request, response, filterChain);
                break;
            case ACTUAL:
                // Handles an Actual CORS request.
                this.handleSimpleCORS(request, response, filterChain);
                break;
            case PRE_FLIGHT:
                // Handles a Pre-flight CORS request.
                this.handlePreflightCORS(request, response, filterChain);
                break;
            case NOT_CORS:
                // Handles a Normal request that is not a cross-origin request.
                this.handleNonCORS(request, response, filterChain);
                break;
            default:
                // Handles a CORS request that violates specification.
                this.handleInvalidCORS(request, response, filterChain);
                break;
        }
    }


    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        parseAndStore(
                getInitParameter(filterConfig, PARAM_CORS_ALLOWED_ORIGINS, CorsProperties.Allowed.DEFAULT_ALLOWED_ORIGINS),
                getInitParameter(filterConfig, PARAM_CORS_ALLOWED_METHODS, CorsProperties.Allowed.DEFAULT_ALLOWED_HTTP_METHODS),
                getInitParameter(filterConfig, PARAM_CORS_ALLOWED_HEADERS, CorsProperties.Allowed.DEFAULT_ALLOWED_HTTP_HEADERS),
                getInitParameter(filterConfig, PARAM_CORS_EXPOSED_HEADERS, ""),
                getInitParameter(filterConfig, PARAM_CORS_SUPPORT_CREDENTIALS, "false"),
                getInitParameter(filterConfig, PARAM_CORS_PREFLIGHT_MAXAGE, "1800"),
                getInitParameter(filterConfig, PARAM_CORS_REQUEST_DECORATE, "true")
        );
    }


    /**
     * This method returns the parameter's value if it exists, or defaultValue
     * if not.
     *
     * @param filterConfig The configuration for the filter
     * @param name         The parameter's name
     * @param defaultValue The default value to return if the parameter does
     *                     not exist
     * @return The parameter's value or the default value if the parameter does
     * not exist
     */
    private String getInitParameter(FilterConfig filterConfig, String name, String defaultValue) {

        if (filterConfig == null) {
            return defaultValue;
        }

        String value = filterConfig.getInitParameter(name);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }


    /**
     * Handles a CORS request of type {@link CORSRequestType}.SIMPLE.
     *
     * @param request     The {@link HttpServletRequest} object.
     * @param response    The {@link HttpServletResponse} object.
     * @param filterChain The {@link FilterChain} object.
     * @throws IOException      an IO error occurred
     * @throws ServletException Servlet error propagation
     * @see <a href="http://www.w3.org/TR/cors/#resource-requests">Simple
     * Cross-Origin Request, Actual Request, and Redirects</a>
     */
    protected void handleSimpleCORS(final HttpServletRequest request,
                                    final HttpServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {

        CorsFilter.CORSRequestType requestType = checkRequestType(request);
        if (!(requestType == CorsFilter.CORSRequestType.SIMPLE ||
                requestType == CorsFilter.CORSRequestType.ACTUAL)) {
            String error = "Expects a HttpServletRequest object of type [{0}] or [{1}]";
            error = StringTemplates.formatWithIndex(error, CORSRequestType.SIMPLE, CORSRequestType.ACTUAL);
            throw new IllegalArgumentException(error);
        }

        final String origin = request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN);
        final String method = request.getMethod();

        // Section 6.1.2
        if (!conf.getAllowed().isOriginAllowed(origin)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }

        if (!this.conf.getAllowed().getMethods().contains(method)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }

        addStandardHeaders(request, response);

        // Forward the request down the filter chain.
        filterChain.doFilter(request, response);
    }


    /**
     * Handles CORS pre-flight request.
     *
     * @param request     The {@link HttpServletRequest} object.
     * @param response    The {@link HttpServletResponse} object.
     * @param filterChain The {@link FilterChain} object.
     * @throws IOException      an IO error occurred
     * @throws ServletException Servlet error propagation
     */
    protected void handlePreflightCORS(final HttpServletRequest request,
                                       final HttpServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {

        CORSRequestType requestType = checkRequestType(request);
        if (requestType != CORSRequestType.PRE_FLIGHT) {
            String error = "Expects a HttpServletRequest object of type [{0}]";
            error = StringTemplates.formatWithIndex(CORSRequestType.PRE_FLIGHT.name().toLowerCase(Locale.ENGLISH));
            throw new IllegalArgumentException(error);
        }

        final String origin = request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN);

        // Section 6.2.2
        if (!conf.getAllowed().isOriginAllowed(origin)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }

        // Section 6.2.3
        String accessControlRequestMethod = request.getHeader(
                CorsFilter.REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
        if (accessControlRequestMethod == null) {
            handleInvalidCORS(request, response, filterChain);
            return;
        } else {
            accessControlRequestMethod = accessControlRequestMethod.trim();
        }

        // Section 6.2.4
        String accessControlRequestHeadersHeader = request.getHeader(
                CorsFilter.REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
        List<String> accessControlRequestHeaders = new LinkedList<>();
        if (accessControlRequestHeadersHeader != null &&
                !accessControlRequestHeadersHeader.trim().isEmpty()) {
            String[] headers = accessControlRequestHeadersHeader.trim().split(",");
            for (String header : headers) {
                accessControlRequestHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
            }
        }

        // Section 6.2.5
        if (!this.conf.getAllowed().getMethods().contains(accessControlRequestMethod)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }

        // Section 6.2.6
        if (!accessControlRequestHeaders.isEmpty()) {
            for (String header : accessControlRequestHeaders) {
                if (!this.conf.getAllowed().getHeaders().contains(header)) {
                    handleInvalidCORS(request, response, filterChain);
                    return;
                }
            }
        }

        addStandardHeaders(request, response);

        // Do not forward the request down the filter chain.
    }


    /**
     * Handles a request, that's not a CORS request, but is a valid request i.e.
     * it is not a cross-origin request. This implementation, just forwards the
     * request down the filter chain.
     *
     * @param request     The {@link HttpServletRequest} object.
     * @param response    The {@link HttpServletResponse} object.
     * @param filterChain The {@link FilterChain} object.
     * @throws IOException      an IO error occurred
     * @throws ServletException Servlet error propagation
     */
    private void handleNonCORS(final HttpServletRequest request,
                               final HttpServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {

        addStandardHeaders(request, response);

        // Let request pass.
        filterChain.doFilter(request, response);
    }


    /**
     * Handles a CORS request that violates specification.
     *
     * @param request     The {@link HttpServletRequest} object.
     * @param response    The {@link HttpServletResponse} object.
     * @param filterChain The {@link FilterChain} object.
     */
    private void handleInvalidCORS(final HttpServletRequest request,
                                   final HttpServletResponse response, final FilterChain filterChain) {
        String origin = request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN);
        String method = request.getMethod();
        String accessControlRequestHeaders = request.getHeader(
                REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);

        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.resetBuffer();

        if (log.isDebugEnabled()) {
            // Debug so no need for i18n
            StringBuilder message = new StringBuilder("Invalid CORS request; Origin=");
            message.append(origin);
            message.append(";Method=");
            message.append(method);
            if (accessControlRequestHeaders != null) {
                message.append(";Access-Control-Request-Headers=");
                message.append(accessControlRequestHeaders);
            }
            log.debug(message.toString());
        }
    }


    /*
     * Sets a standard set of headers to reduce response variation which in turn
     * is intended to aid caching.
     */
    private void addStandardHeaders(final HttpServletRequest request,
                                    final HttpServletResponse response) {

        final String method = request.getMethod();
        final String origin = request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN);

        if (!conf.anyOriginAllowed()) {
            // If only specific origins are allowed, the response will vary by
            // origin
            CorsResponses.addVaryFieldName(response, CorsFilter.REQUEST_HEADER_ORIGIN);
        }

        // CORS requests (SIMPLE, ACTUAL, PRE_FLIGHT) set the following headers
        // although non-CORS requests do not need to. The headers are always set
        // as a) they do no harm in the non-CORS case and b) it allows the same
        // response to be cached for CORS and non-CORS requests.

        // Add a single Access-Control-Allow-Origin header.
        if (conf.anyOriginAllowed()) {
            // If any origin is allowed, return header with '*'.
            response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        } else {
            // Add a single Access-Control-Allow-Origin header, with the value
            // of the Origin header as value.
            response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }

        // If the resource supports credentials, add a single
        // Access-Control-Allow-Credentials header with the case-sensitive
        // string "true" as value.
        if (this.conf.getSupport().isCredentials()) {
            response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        // If the list of exposed headers is not empty add one or more
        // Access-Control-Expose-Headers headers, with as values the header
        // field names given in the list of exposed headers.
        List<String> exposedHeaders = this.conf.getExposed().getHeaders();
        if (Objs.isNotEmpty(exposedHeaders)) {
            String exposedHeadersString = join(exposedHeaders, ",");
            response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS,
                    exposedHeadersString);
        }

        if ("OPTIONS".equals(method)) {
            // For an OPTIONS request, the response will vary based on the
            // value or absence of the following headers. Hence they need be be
            // included in the Vary header.
            CorsResponses.addVaryFieldName(
                    response, CorsFilter.REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
            CorsResponses.addVaryFieldName(
                    response, CorsFilter.REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);

            // CORS PRE_FLIGHT (OPTIONS) requests set the following headers although
            // non-CORS OPTIONS requests do not need to. The headers are always set
            // as a) they do no harm in the non-CORS case and b) it allows the same
            // response to be cached for CORS and non-CORS requests.

            if (this.conf.getPreflight().getMaxage() > 0) {
                response.addHeader(
                        CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE,
                        String.valueOf(this.conf.getPreflight().getMaxage()));
            }

            if (!this.conf.getAllowed().getMethods().isEmpty()) {
                response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS, join(this.conf.getAllowed().getMethods(), ","));
            }

            List<String> allowedHttpHeaders = this.conf.getAllowed().getHeaders();
            if (!allowedHttpHeaders.isEmpty()) {
                response.addHeader(CorsFilter.RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS, join(allowedHttpHeaders, ","));
            }
        }
    }


    @Override
    public void destroy() {
        // NOOP
    }


    /**
     * Decorates the {@link HttpServletRequest}, with CORS attributes.
     * <ul>
     * <li><b>cors.isCorsRequest:</b> Flag to determine if request is a CORS
     * request. Set to <code>true</code> if CORS request; <code>false</code>
     * otherwise.</li>
     * <li><b>cors.request.origin:</b> The Origin URL.</li>
     * <li><b>cors.request.type:</b> Type of request. Values:
     * <code>simple</code> or <code>preflight</code> or <code>not_cors</code> or
     * <code>invalid_cors</code></li>
     * <li><b>cors.request.headers:</b> Request headers sent as
     * 'Access-Control-Request-Headers' header, for pre-flight request.</li>
     * </ul>
     *
     * @param request         The {@link HttpServletRequest} object.
     * @param corsRequestType The {@link CORSRequestType} object.
     */
    protected static void decorateCORSProperties(
            final HttpServletRequest request,
            final CORSRequestType corsRequestType) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest object is null");
        }

        if (corsRequestType == null) {
            throw new IllegalArgumentException("CORSRequestType object is null");
        }

        switch (corsRequestType) {
            case SIMPLE:
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.TRUE);
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_ORIGIN, request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN));
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, corsRequestType.name().toLowerCase(Locale.ENGLISH));
                break;
            case ACTUAL:
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_ORIGIN, request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN));
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.TRUE);
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, corsRequestType.name().toLowerCase(Locale.ENGLISH));
                break;
            case PRE_FLIGHT:
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.TRUE);
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_ORIGIN, request.getHeader(CorsFilter.REQUEST_HEADER_ORIGIN));
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, corsRequestType.name().toLowerCase(Locale.ENGLISH));
                String headers = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
                if (headers == null) {
                    headers = "";
                }
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS, headers);
                break;
            case NOT_CORS:
                request.setAttribute(CorsFilter.HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.FALSE);
                break;
            default:
                // Don't set any attributes
                break;
        }
    }


    /**
     * Joins elements of {@link Set} into a string, where each element is
     * separated by the provided separator.
     *
     * @param elements      The {@link Set} containing elements to join together.
     * @param joinSeparator The character to be used for separating elements.
     * @return The joined {@link String}; <code>null</code> if elements
     * {@link Set} is null.
     */
    protected static String join(final Collection<String> elements, final String joinSeparator) {
        String separator = ",";
        if (elements == null) {
            return null;
        }
        if (joinSeparator != null) {
            separator = joinSeparator;
        }
        StringBuilder buffer = new StringBuilder();
        boolean isFirst = true;
        for (String element : elements) {
            if (!isFirst) {
                buffer.append(separator);
            } else {
                isFirst = false;
            }

            if (element != null) {
                buffer.append(element);
            }
        }

        return buffer.toString();
    }


    /**
     * Determines the request type.
     *
     * @param request The HTTP Servlet request
     * @return the CORS type
     */
    protected CORSRequestType checkRequestType(final HttpServletRequest request) {
        CORSRequestType requestType = CORSRequestType.INVALID_CORS;
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest object is null");
        }
        String originHeader = request.getHeader(REQUEST_HEADER_ORIGIN);
        // Section 6.1.1 and Section 6.2.1
        if (originHeader != null) {
            if (originHeader.isEmpty()) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (!isValidOrigin(originHeader)) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (isLocalOrigin(request, originHeader)) {
                return CORSRequestType.NOT_CORS;
            } else {
                String method = request.getMethod();
                if (method != null) {
                    if ("OPTIONS".equals(method)) {
                        String accessControlRequestMethodHeader = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
                        if (accessControlRequestMethodHeader != null && !accessControlRequestMethodHeader.isEmpty()) {
                            requestType = CORSRequestType.PRE_FLIGHT;
                        } else if (accessControlRequestMethodHeader != null && accessControlRequestMethodHeader.isEmpty()) {
                            requestType = CORSRequestType.INVALID_CORS;
                        } else {
                            requestType = CORSRequestType.ACTUAL;
                        }
                    } else if ("GET".equals(method) || "HEAD".equals(method)) {
                        requestType = CORSRequestType.SIMPLE;
                    } else if ("POST".equals(method)) {
                        String mediaType = getMediaType(request.getContentType());
                        if (mediaType != null) {
                            if (SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(mediaType)) {
                                requestType = CORSRequestType.SIMPLE;
                            } else {
                                requestType = CORSRequestType.ACTUAL;
                            }
                        }
                    } else {
                        requestType = CORSRequestType.ACTUAL;
                    }
                }
            }
        } else {
            requestType = CORSRequestType.NOT_CORS;
        }

        return requestType;
    }


    private boolean isLocalOrigin(HttpServletRequest request, String origin) {

        // Build scheme://host:port from request
        StringBuilder target = new StringBuilder();
        String scheme = request.getScheme();
        if (scheme == null) {
            return false;
        } else {
            scheme = scheme.toLowerCase(Locale.ENGLISH);
        }
        target.append(scheme);
        target.append("://");

        String host = request.getServerName();
        if (host == null) {
            return false;
        }
        target.append(host);

        int port = request.getServerPort();
        if ("http".equals(scheme) && port != 80 || "https".equals(scheme) && port != 443) {
            target.append(':');
            target.append(port);
        }

        return origin.equalsIgnoreCase(target.toString());
    }


    /**
     * Return the lower case, trimmed value of the media type from the content
     * type.
     */
    private String getMediaType(String contentType) {
        if (contentType == null) {
            return null;
        }
        String result = contentType.toLowerCase(Locale.ENGLISH);
        int firstSemiColonIndex = result.indexOf(';');
        if (firstSemiColonIndex > -1) {
            result = result.substring(0, firstSemiColonIndex);
        }
        result = result.trim();
        return result;
    }


    /**
     * Parses each param-value and populates configuration variables. If a param
     * is provided, it overrides the default.
     *
     * @param allowedOrigins      A {@link String} of comma separated origins.
     * @param allowedHttpMethods  A {@link String} of comma separated HTTP methods.
     * @param allowedHttpHeaders  A {@link String} of comma separated HTTP headers.
     * @param exposedHeaders      A {@link String} of comma separated headers that needs to be
     *                            exposed.
     * @param supportsCredentials "true" if support credentials needs to be enabled.
     * @param preflightMaxAge     The amount of seconds the user agent is allowed to cache the
     *                            result of the pre-flight request.
     * @throws ServletException
     */
    private void parseAndStore(final String allowedOrigins,
                               final String allowedHttpMethods, final String allowedHttpHeaders,
                               final String exposedHeaders, final String supportsCredentials,
                               final String preflightMaxAge, final String decorateRequest)
            throws ServletException {

        CorsProperties corsProperties = new CorsProperties();

        CorsProperties.Allowed allowed = new CorsProperties.Allowed();
        allowed.setOrigins(allowedOrigins);
        allowed.setMethods(allowedHttpMethods);
        allowed.setHeaders(allowedHttpHeaders);
        corsProperties.setAllowed(allowed);

        CorsProperties.Exposed exposed = new CorsProperties.Exposed();
        exposed.setHeaders(exposedHeaders);
        corsProperties.setExposed(exposed);

        // For any value other then 'true' this will be false.
        CorsProperties.Support support = new CorsProperties.Support();
        support.setCredentials(Boolean.parseBoolean(supportsCredentials));
        corsProperties.setSupport(support);


        CorsProperties.Preflight preflight = new CorsProperties.Preflight();
        long preflightMaxAgeLong = 0L;
        try {
            if (!preflightMaxAge.isEmpty()) {
                preflightMaxAgeLong = Long.parseLong(preflightMaxAge);
            }
        } catch (NumberFormatException e) {
            throw new ServletException("Unable to parse preflightMaxAge", e);
        }
        preflight.setMaxage(preflightMaxAgeLong);
        corsProperties.setPreflight(preflight);

        CorsProperties.Request request = new CorsProperties.Request();
        // For any value other then 'true' this will be false.
        boolean decorateRequestBoolean = Boolean.parseBoolean(decorateRequest);
        request.setDecorate(decorateRequestBoolean);
        corsProperties.setRequest(request);

        this.conf = corsProperties;
        if (this.conf.getSupport().isCredentials() && this.conf.anyOriginAllowed()) {
            throw new ServletException("It is not allowed to configure supportsCredentials=[true] when allowedOrigins=[*]");
        }
    }


    /**
     * Checks if a given origin is valid or not. Criteria:
     * <ul>
     * <li>If an encoded character is present in origin, it's not valid.</li>
     * <li>If origin is "null", it's valid.</li>
     * <li>Origin should be a valid {@link URI}</li>
     * </ul>
     *
     * @param origin The origin URI
     * @return <code>true</code> if the origin was valid
     * @see <a href="http://tools.ietf.org/html/rfc952">RFC952</a>
     */
    protected static boolean isValidOrigin(String origin) {
        // Checks for encoded characters. Helps prevent CRLF injection.
        if (origin.contains("%")) {
            return false;
        }

        // "null" is a valid origin
        if ("null".equals(origin)) {
            return true;
        }

        // RFC6454, section 4. "If uri-scheme is file, the implementation MAY
        // return an implementation-defined value.". No limits are placed on
        // that value so treat all file URIs as valid origins.
        if (origin.startsWith("file://")) {
            return true;
        }

        URI originURI;
        try {
            originURI = new URI(origin);
        } catch (URISyntaxException e) {
            return false;
        }
        // If scheme for URI is null, return false. Return true otherwise.
        return originURI.getScheme() != null;

    }


    // -------------------------------------------------- CORS Response Headers
    /**
     * The Access-Control-Allow-Origin header indicates whether a resource can
     * be shared based by returning the value of the Origin request header in
     * the response.
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    /**
     * The Access-Control-Allow-Credentials header indicates whether the
     * response to request can be exposed when the omit credentials flag is
     * unset. When part of the response to a preflight request it indicates that
     * the actual request can include user credentials.
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    /**
     * The Access-Control-Expose-Headers header indicates which headers are safe
     * to expose to the API of a CORS API specification
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    /**
     * The Access-Control-Max-Age header indicates how long the results of a
     * preflight request can be cached in a preflight result cache.
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    /**
     * The Access-Control-Allow-Methods header indicates, as part of the
     * response to a preflight request, which methods can be used during the
     * actual request.
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    /**
     * The Access-Control-Allow-Headers header indicates, as part of the
     * response to a preflight request, which header field names can be used
     * during the actual request.
     */
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    // -------------------------------------------------- CORS Request Headers

    /**
     * The Vary header indicates allows disabling proxy caching by indicating
     * the the response depends on the origin.
     *
     * @deprecated Unused. Will be removed in Tomcat 10
     */
    @Deprecated
    public static final String REQUEST_HEADER_VARY = "Vary";

    /**
     * The Origin header indicates where the cross-origin request or preflight
     * request originates from.
     */
    public static final String REQUEST_HEADER_ORIGIN = "Origin";

    /**
     * The Access-Control-Request-Method header indicates which method will be
     * used in the actual request as part of the preflight request.
     */
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    /**
     * The Access-Control-Request-Headers header indicates which headers will be
     * used in the actual request as part of the preflight request.
     */
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    // ----------------------------------------------------- Request attributes
    /**
     * The prefix to a CORS request attribute.
     */
    public static final String HTTP_REQUEST_ATTRIBUTE_PREFIX = "cors.";

    /**
     * Attribute that contains the origin of the request.
     */
    public static final String HTTP_REQUEST_ATTRIBUTE_ORIGIN = HTTP_REQUEST_ATTRIBUTE_PREFIX + "request.origin";

    /**
     * Boolean value, suggesting if the request is a CORS request or not.
     */
    public static final String HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST = HTTP_REQUEST_ATTRIBUTE_PREFIX + "isCorsRequest";

    /**
     * Type of CORS request, of type {@link CORSRequestType}.
     */
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE = HTTP_REQUEST_ATTRIBUTE_PREFIX + "request.type";

    /**
     * Request headers sent as 'Access-Control-Request-Headers' header, for
     * pre-flight request.
     */
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS = HTTP_REQUEST_ATTRIBUTE_PREFIX + "request.headers";

    // -------------------------------------------------------------- Constants

    /**
     * Enumerates varies types of CORS requests. Also, provides utility methods
     * to determine the request type.
     */
    protected enum CORSRequestType {
        /**
         * A simple HTTP request, i.e. it shouldn't be pre-flighted.
         */
        SIMPLE,
        /**
         * A HTTP request that needs to be pre-flighted.
         */
        ACTUAL,
        /**
         * A pre-flight CORS request, to get meta information, before a
         * non-simple HTTP request is sent.
         */
        PRE_FLIGHT,
        /**
         * Not a CORS request, but a normal request.
         */
        NOT_CORS,
        /**
         * An invalid CORS request, i.e. it qualifies to be a CORS request, but
         * fails to be a valid one.
         */
        INVALID_CORS
    }

    /**
     * {@link Collection} of media type values for the Content-Type header that
     * will be treated as 'simple'. Note media-type values are compared ignoring
     * parameters and in a case-insensitive manner.
     *
     * @see <a href="http://www.w3.org/TR/cors/#terminology"
     * >http://www.w3.org/TR/cors/#terminology</a>
     */
    public static final Collection<String> SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES = new HashSet<>(Collects.asList("application/x-www-form-urlencoded", "multipart/form-data", "text/plain"));

    // ----------------------------------------Filter Config Init param-name(s)
    /**
     * Key to retrieve allowed origins from {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_ALLOWED_ORIGINS = "cors.allowed.origins";

    /**
     * Key to retrieve support credentials from
     * {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_SUPPORT_CREDENTIALS = "cors.support.credentials";

    /**
     * Key to retrieve exposed headers from {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_EXPOSED_HEADERS = "cors.exposed.headers";

    /**
     * Key to retrieve allowed headers from {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_ALLOWED_HEADERS = "cors.allowed.headers";

    /**
     * Key to retrieve allowed methods from {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_ALLOWED_METHODS = "cors.allowed.methods";

    /**
     * Key to retrieve preflight max age from
     * {@link javax.servlet.FilterConfig}.
     */
    public static final String PARAM_CORS_PREFLIGHT_MAXAGE = "cors.preflight.maxage";

    /**
     * Key to determine if request should be decorated.
     */
    public static final String PARAM_CORS_REQUEST_DECORATE = "cors.request.decorate";
}
