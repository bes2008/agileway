package com.jn.agileway.oauth2.authz;

import com.bes.um3rd.utils.HttpHeaderUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class OAuth2AuthzFilter extends OncePerRequestFilter {

    private final OAuth2AuthzHandler oauth2AuthzHandler;
    private final OAuth2Properties oauth2Properties;

    public OAuth2AuthzFilter(OAuth2Properties oauth2Properties, OAuth2AuthzHandler oauth2AuthzHandler) {
        this.oauth2AuthzHandler = oauth2AuthzHandler;
        this.oauth2Properties = oauth2Properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isCallbackUri(request)) {
            String state = request.getParameter("state");
            oauth2AuthzHandler.handleAuthorizationCodeResponse(request, response, state);
            return;
        }
        String accessToken = getAccessToken(request);
        if (oauth2AuthzHandler.validateAccessTokenAndExtractUserInfo(request, response, accessToken, true)) {
            filterChain.doFilter(request, response);
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getParameter(oauth2Properties.getAccessTokenParameterName());
        if (StringUtils.isBlank(accessToken)) {
            String authorization = request.getHeader("Authorization");
            accessToken = HttpHeaderUtils.getBearerToken(authorization);
        }
        if (StringUtils.isBlank(accessToken)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(oauth2Properties.getAccessTokenCookieName()))
                        .findFirst().orElse(null);
                if (cookie != null) {
                    accessToken = cookie.getValue();
                }
            }
        }
        return accessToken;
    }

    private boolean isCallbackUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String callbackUri = oauth2Properties.getCallbackUri();
        String contextPath = request.getContextPath();
        String callbackUriWithContextPath = StringUtils.stripEnd(contextPath, "/") + "/" + StringUtils.stripStart(callbackUri, "/");
        return requestUri.equals(callbackUriWithContextPath);
    }
}
