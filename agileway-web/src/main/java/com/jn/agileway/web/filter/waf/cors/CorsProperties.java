package com.jn.agileway.web.filter.waf.cors;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;

import java.util.List;

/**
 * https://tomcat.apache.org/tomcat-9.0-doc/config/filter.html#CORS_Filter
 *
 * configuration prefix: cors
 */
public class CorsProperties {
    private boolean enabled;
    private Allowed allowed;
    private Exposed exposed;
    private Preflight preflight;
    private Support support;
    private Request request;

    public CorsProperties(){
        setAllowed(new Allowed());
        setExposed(new Exposed());
        setPreflight(new Preflight());
        setSupport(new Support());
        setRequest(new Request());
        setEnabled(false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean anyOriginAllowed(){
        return allowed.isAnyOriginAllowed();
    }
    public Allowed getAllowed() {
        return allowed;
    }

    public void setAllowed(Allowed allowed) {
        this.allowed = allowed;
    }

    public Exposed getExposed() {
        return exposed;
    }

    public void setExposed(Exposed exposed) {
        this.exposed = exposed;
    }

    public Preflight getPreflight() {
        return preflight;
    }

    public void setPreflight(Preflight preflight) {
        this.preflight = preflight;
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    private static List<String> splitAsList(String str){
        return splitAsList(str,false);
    }
    private static List<String> splitAsList(String str, boolean toLowerCase) {
        str = Strings.trimToEmpty(str);
        Pipeline<String> pipeline = Pipeline.of(Strings.split(str, ","));
        if(toLowerCase){
            pipeline = pipeline.map(Functions.toLowerCase());
        }
        return pipeline.asList();
    }


    public static class Allowed {

        /**
         * By default, no origins are allowed to make requests.
         */
        public static final String DEFAULT_ALLOWED_ORIGINS = "";
        private List<String> origins;

        /**
         * A list of origins that are allowed to access the resource. A * can be specified to enable access to resource from any origin. Otherwise, an allow list of comma separated origins can be provided. Eg: https://www.w3.org, https://www.apache.org. Defaults: The empty String. (No origin is allowed to access the resource).
         */
        public void setOrigins(String origins) {
            setOrigins(splitAsList(origins));
        }

        private List<String> methods;
        /**
         * By default, following methods are supported: GET, POST, HEAD and OPTIONS.
         */
        public static final String DEFAULT_ALLOWED_HTTP_METHODS = "GET,POST,HEAD,OPTIONS";
        /*
         * A comma separated list of HTTP methods that can be used to access the resource, using cross-origin requests. These are the methods which will also be included as part of Access-Control-Allow-Methods header in pre-flight response. Eg: GET, POST. Defaults: GET, POST, HEAD, OPTIONS
         */
        public void setMethods(String methods) {
            setMethods(splitAsList(methods));
        }

        private List<String> headers;
        /**
         * By default, following headers are supported:
         * Origin,Accept,X-Requested-With, Content-Type,
         * Access-Control-Request-Method, and Access-Control-Request-Headers.
         */
        public static final String DEFAULT_ALLOWED_HTTP_HEADERS = "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers";
        /**
         * A comma separated list of request headers that can be used when making an actual request. These headers will also be returned as part of Access-Control-Allow-Headers header in a pre-flight response. Eg: Origin,Accept. Defaults: Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers
         */
        public void setHeaders(String headers) {
            setHeaders(splitAsList(headers, true));
        }

        public Allowed() {
            setOrigins(DEFAULT_ALLOWED_ORIGINS);
            setHeaders(DEFAULT_ALLOWED_HTTP_HEADERS);
            setMethods(DEFAULT_ALLOWED_HTTP_METHODS);
        }

        public boolean isOriginAllowed(final String origin){
            if (isAnyOriginAllowed()) {
                return true;
            }

            // If 'Origin' header is a case-sensitive match of any of allowed
            // origins, then return true, else return false.
            return this.origins.contains(origin);
        }

        public List<String> getOrigins() {
            return origins;
        }

        public void setOrigins(List<String> origins) {
            this.origins = origins;
        }


        public List<String> getMethods() {
            return methods;
        }

        public void setMethods(List<String> methods) {
            this.methods = methods;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public boolean isAnyOriginAllowed(){
            return Objs.isNotEmpty(this.origins) && "*".equals(this.origins.get(0));
        }
    }

    public static class Exposed {
        private List<String> headers;

        public Exposed(){
            setHeaders("");
        }

        /**
         * A comma separated list of request headers that can be used when making an actual request. These headers will also be returned as part of Access-Control-Allow-Headers header in a pre-flight response. Eg: Origin,Accept. Defaults: Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers
         */
        public void setHeaders(String headers) {
            setHeaders(splitAsList(headers));
        }

        public List<String> getHeaders() {
            return headers;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }
    }

    public static class Preflight {
        /**
         * The amount of seconds, browser is allowed to cache the result of the pre-flight request. This will be included as part of Access-Control-Max-Age header in the pre-flight response. A negative value will prevent CORS Filter from adding this response header to pre-flight response. Defaults: 1800
         */
        private long maxage = 1800L;

        public long getMaxage() {
            return maxage;
        }

        public void setMaxage(long maxage) {
            this.maxage = maxage;
        }
    }

    public static class Support{
        /**
         * A flag that indicates whether the resource supports user credentials. This flag is exposed as part of Access-Control-Allow-Credentials header in a pre-flight response. It helps browser determine whether or not an actual request can be made using credentials. Defaults: false
         */
        private boolean credentials = false;

        public boolean isCredentials() {
            return credentials;
        }

        public void setCredentials(boolean credentials) {
            this.credentials = credentials;
        }
    }

    public static class Request{
        /**
         * A flag to control if CORS specific attributes should be added to HttpServletRequest object or not. Defaults: true
         */
        private boolean decorate = true;

        public boolean isDecorate() {
            return decorate;
        }

        public void setDecorate(boolean decorate) {
            this.decorate = decorate;
        }
    }
}
