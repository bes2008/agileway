package com.jn.agileway.web.filter.waf.allowedmethods;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @since 4.0.1
 */
public class AllowedMethodsFilter extends OncePerRequestFilter {
    private List<HttpMethod> allowedMethods;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (Objs.isEmpty(allowedMethods)) {
            String methodString = filterConfig.getInitParameter("allowed.methods");
            List<HttpMethod> allowedMethods = parseAllowedMethods(methodString);
            setAllowedMethods(allowedMethods);
        }
    }

    public static List<HttpMethod> parseAllowedMethods(String methodString) {
        String[] methods = Strings.split(methodString, ",");
        List<HttpMethod> allowedMethods = Pipeline.of(methods)
                .map(new Function<String, HttpMethod>() {
                    @Override
                    public HttpMethod apply(String method) {
                        return HttpMethod.resolve(Strings.upperCase(method));
                    }
                }).clearNulls().asList();
        if (Objs.isEmpty(allowedMethods)) {
            allowedMethods = Collects.newArrayList(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PATCH);
        }
        return allowedMethods;
    }

    public void setAllowedMethods(List<HttpMethod> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();
        HttpMethod m = HttpMethod.resolve(method);
        if (m != null && allowedMethods.contains(m)) {
            chain.doFilter(request, response);
        } else {
            throw new HttpMethodNotSupportedException(method, allowedMethods);
        }
    }
}
