/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.agileway.web.filter.waf.csrf;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.security.AccessDeniedException;
import com.jn.agileway.web.security.AccessDeniedHandler;
import com.jn.agileway.web.security.AccessDeniedHandlerImpl;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.function.Predicate;

import com.jn.langx.util.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Applies
 * <a href="https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)" >CSRF</a>
 * protection using a synchronizer token pattern. Developers are required to ensure that
 * {@link CsrfFilter} is invoked for any request that allows state to change. Typically
 * this just means that they should ensure their web application follows proper REST
 * semantics (i.e. do not change state with the HTTP methods GET, HEAD, TRACE, OPTIONS).
 * </p>
 *
 * <p>
 * Typically the {@link CsrfTokenRepository} implementation chooses to store the
 * {@link CsrfToken} in {@link HttpSession} with {@link HttpSessionCsrfTokenRepository}
 * wrapped by a {@link LazyCsrfTokenRepository}. This is preferred to storing the token in
 * a cookie which can be modified by a client application.
 * </p>
 */
public final class CsrfFilter extends OncePerRequestFilter {

    /**
     * The default {@link Predicate<HttpServletRequest> } that indicates if CSRF protection is required or
     * not. The default is to ignore GET, HEAD, TRACE, OPTIONS and process all other
     * requests.
     */
    public static final Predicate<HttpServletRequest> DEFAULT_CSRF_MATCHER = new DefaultRequiresCsrfMatcher();

    /**
     * The attribute name to use when marking a given request as one that should not be
     * filtered.
     * <p>
     * To use, set the attribute on your {@link HttpServletRequest}: <pre>
     * 	CsrfFilter.skipRequest(request);
     * </pre>
     */
    private static final String SHOULD_NOT_FILTER = "SHOULD_NOT_FILTER" + CsrfFilter.class.getName();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CsrfTokenRepository tokenRepository;

    private Predicate<HttpServletRequest> requireCsrfProtectionMatcher = DEFAULT_CSRF_MATCHER;

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

    public CsrfFilter(CsrfTokenRepository csrfTokenRepository) {
        Preconditions.checkNotNull(csrfTokenRepository, "csrfTokenRepository cannot be null" );
        this.tokenRepository = csrfTokenRepository;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Boolean.TRUE.equals(request.getAttribute(SHOULD_NOT_FILTER));
    }

    protected void doFilterInternal(ServletRequest request0, ServletResponse response0, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) request0;
        HttpServletResponse response = (HttpServletResponse) response0;
        request.setAttribute(HttpServletResponse.class.getName(), response);
        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        boolean missingToken = (csrfToken == null);
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(request);
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);
        if (!this.requireCsrfProtectionMatcher.test(request)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Did not protect against CSRF since request did not match "
                        + this.requireCsrfProtectionMatcher);
            }
            filterChain.doFilter(request, response);
            return;
        }
        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if (actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }
        if (!equalsConstantTime(csrfToken.getToken(), actualToken)) {
            this.logger.debug("Invalid CSRF token found for {}", Servlets.buildFullRequestUrl(request));
            AccessDeniedException exception = (!missingToken) ? new InvalidCsrfTokenException(csrfToken, actualToken)
                    : new MissingCsrfTokenException(actualToken);
            this.accessDeniedHandler.handle(request, response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    public static void skipRequest(HttpServletRequest request) {
        request.setAttribute(SHOULD_NOT_FILTER, Boolean.TRUE);
    }

    /**
     * Specifies a {@link Predicate<HttpServletRequest>} that is used to determine if CSRF protection
     * should be applied. If the {@link Predicate<HttpServletRequest>} returns true for a given request,
     * then CSRF protection is applied.
     *
     * <p>
     * The default is to apply CSRF protection for any HTTP method other than GET, HEAD,
     * TRACE, OPTIONS.
     * </p>
     *
     * @param requireCsrfProtectionMatcher the {@link Predicate<HttpServletRequest>} used to determine if
     *                                     CSRF protection should be applied.
     */
    public void setRequireCsrfProtectionMatcher(Predicate<HttpServletRequest> requireCsrfProtectionMatcher) {
        Preconditions.checkNotNull(requireCsrfProtectionMatcher, "requireCsrfProtectionMatcher cannot be null" );
        this.requireCsrfProtectionMatcher = requireCsrfProtectionMatcher;
    }

    /**
     * Specifies a {@link AccessDeniedHandler} that should be used when CSRF protection
     * fails.
     *
     * <p>
     * The default is to use AccessDeniedHandlerImpl with no arguments.
     * </p>
     *
     * @param accessDeniedHandler the {@link AccessDeniedHandler} to use
     */
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        Preconditions.checkNotNull(accessDeniedHandler, "accessDeniedHandler cannot be null" );
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Constant time comparison to prevent against timing attacks.
     *
     * @param expected
     * @param actual
     * @return
     */
    private static boolean equalsConstantTime(String expected, String actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        // Encode after ensure that the string is not null
        byte[] expectedBytes = Charsets.encode(Charsets.UTF_8, expected);
        byte[] actualBytes = Charsets.encode(Charsets.UTF_8, actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static final class DefaultRequiresCsrfMatcher implements Predicate<HttpServletRequest> {

        private final HashSet<String> allowedMethods = new HashSet<String>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS" ));

        @Override
        public boolean test(HttpServletRequest request) {
            return !this.allowedMethods.contains(request.getMethod());
        }

        @Override
        public String toString() {
            return "CsrfNotRequired " + this.allowedMethods;
        }

    }

}
