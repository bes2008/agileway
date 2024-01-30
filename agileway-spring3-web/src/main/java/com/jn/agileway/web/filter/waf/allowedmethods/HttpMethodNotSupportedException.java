package com.jn.agileway.web.filter.waf.allowedmethods;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.*;

/**
 * @since 4.0.1
 */
public class HttpMethodNotSupportedException extends RuntimeException {
    private String method;

    private String[] supportedMethods;


    /**
     * Create a new HttpRequestMethodNotSupportedException.
     *
     * @param method the unsupported HTTP request method
     */
    public HttpMethodNotSupportedException(String method) {
        this(method, (String[]) null);
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     *
     * @param method the unsupported HTTP request method
     * @param msg    the detail message
     */
    public HttpMethodNotSupportedException(String method, String msg) {
        this(method, null, msg);
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     *
     * @param method           the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods (may be {@code null})
     */
    public HttpMethodNotSupportedException(String method, Collection<String> supportedMethods) {
        this(method, Strings.toStringArray(supportedMethods));
    }

    public HttpMethodNotSupportedException(String method, List<HttpMethod> supportedMethods) {
        this(method, Strings.split(Strings.join(",", supportedMethods),","));
    }
    /**
     * Create a new HttpRequestMethodNotSupportedException.
     *
     * @param method           the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods (may be {@code null})
     */
    public HttpMethodNotSupportedException(String method, String[] supportedMethods) {
        this(method, supportedMethods, "Request method '" + method + "' not supported");
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     *
     * @param method           the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods
     * @param msg              the detail message
     */
    public HttpMethodNotSupportedException(String method, String[] supportedMethods, String msg) {
        super(msg);
        this.method = method;
        this.supportedMethods = supportedMethods;
    }


    /**
     * Return the HTTP request method that caused the failure.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Return the actually supported HTTP methods, or {@code null} if not known.
     */
    public String[] getSupportedMethods() {
        return this.supportedMethods;
    }

    /**
     * Return the actually supported HTTP methods as {@link HttpMethod} instances,
     * or {@code null} if not known.
     *
     *
     */
    public Set<HttpMethod> getSupportedHttpMethods() {
        if (this.supportedMethods == null) {
            return null;
        }
        List<HttpMethod> supportedMethods = new LinkedList<HttpMethod>();
        for (String value : this.supportedMethods) {
            HttpMethod resolved = HttpMethod.resolve(value);
            if (resolved != null) {
                supportedMethods.add(resolved);
            }
        }
        return EnumSet.copyOf(supportedMethods);
    }

}
