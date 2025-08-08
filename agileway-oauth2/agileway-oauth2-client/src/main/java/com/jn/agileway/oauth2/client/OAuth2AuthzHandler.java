package com.jn.agileway.oauth2.client;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.JWTs;
import com.jn.agileway.oauth2.client.api.OAuth2ApiService;
import com.jn.agileway.oauth2.client.exception.ExpiredAccessTokenException;
import com.jn.agileway.oauth2.client.exception.InvalidAccessTokenException;
import com.jn.agileway.oauth2.client.userinfo.*;
import com.jn.agileway.oauth2.client.validator.BearerAccessTokenValidator;
import com.jn.agileway.oauth2.client.validator.IntrospectAccessTokenValidator;
import com.jn.agileway.oauth2.client.validator.OAuth2StateValidator;
import com.jn.agileway.oauth2.client.validator.OAuth2TokenValidator;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.id.uuidv7.UUIDv7Generator;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *      +----------+
 *      | Resource |
 *      |   Owner  |
 *      |          |
 *      +----------+
 *           ^
 *           |
 *          (B)
 *      +----|-----+          Client Identifier      +---------------+
 *      |         -+----(A)-- & Redirection URI ---->|               |
 *      |  User-   |                                 | Authorization |
 *      |  Agent  -+----(B)-- User authenticates --->|     Server    |
 *      |          |                                 |               |
 *      |         -+----(C)-- Authorization Code ---<|               |
 *      +-|----|---+                                 +---------------+
 *        |    |                                         ^      v
 *       (A)  (C)                                        |      |
 *        |    |                                         |      |
 *        ^    v                                         |      |
 *      +---------+                                      |      |
 *      |         |>---(D)-- Authorization Code ---------'      |
 *      |  Client |          & Redirection URI                  |
 *      |         |                                             |
 *      |         |<---(E)----- Access Token -------------------'
 *      +---------+       (w/ Optional Refresh Token)
 *
 *    Note: The lines illustrating steps (A), (B), and (C) are broken into
 *    two parts as they pass through the user-agent.
 *
 *                      Figure 3: Authorization Code Flow
 *
 * </pre>
 */
public class OAuth2AuthzHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthzHandler.class);
    private static final String SESSION_KEY_OAUTH2_CODE_STATE = "oauth2_code_state";
    /**
     * key: oauth2_login_channel
     * value: HttpServletRequest
     * 用于存储引发授权的请求
     */
    public static final String SESSION_KEY_OAUTH2_ORIGINAL_REQUEST = "oauth2_authorize_original_request";
    public static final String REQUEST_KEY_OAUTH2_AUTHORIZED_USER = "oauth2_authorized_userinfo";

    private OAuth2CallbackUriSupplier callbackUriSupplier;

    private OAuth2ClientProperties oauth2Properties;

    private String authorizeUriTemplate;
    private OAuth2ApiService oauth2ApiService;

    private OAuth2StateValidator oAuth2StateValidator;

    private OAuth2TokenValidator oAuth2TokenValidator;

    private BearerAccessTokenValidator bearerAccessTokenValidator;

    private IntrospectAccessTokenValidator introspectAccessTokenValidator;


    private OpenIdTokenParser openIdTokenParser;

    private OpenIdTokenUserinfoExtractor openIdTokenUserInfoExtractor;

    private IntrospectResultUserInfoExtractor introspectResultUserInfoExtractor;

    /**
     * key: access_token
     * value: OAuth2Token
     */
    private final Map<String, OAuth2TokenCachedValue> tokenCache = new ConcurrentHashMap<>();

    public OAuth2AuthzHandler(
            OAuth2ClientProperties oauth2Properties,
                              OAuth2ApiService oauth2ApiService,
                              OAuth2StateValidator oAuth2StateValidator,
                              OAuth2TokenValidator oAuth2TokenValidator,
                              BearerAccessTokenValidator bearerAccessTokenValidator,
                              IntrospectAccessTokenValidator introspectAccessTokenValidator,
                              OpenIdTokenParser openIdTokenParser,
                              OpenIdTokenUserinfoExtractor openIdTokenUserExtractor,
                              IntrospectResultUserInfoExtractor introspectResultUserInfoExtractor,
                              OAuth2CallbackUriSupplier oauth2CallbackUriSupplier
    ) {

        this.oauth2Properties = oauth2Properties;
        this.oauth2ApiService = oauth2ApiService;

        this.oAuth2StateValidator = oAuth2StateValidator;

        this.oAuth2TokenValidator = oAuth2TokenValidator;
        this.bearerAccessTokenValidator = bearerAccessTokenValidator;
        this.introspectAccessTokenValidator = introspectAccessTokenValidator;

        this.openIdTokenParser = openIdTokenParser;

        this.openIdTokenUserInfoExtractor = openIdTokenUserExtractor;
        this.introspectResultUserInfoExtractor = introspectResultUserInfoExtractor;

        this.authorizeUriTemplate = oauth2Properties.getBaseUri() + oauth2Properties.getAuthorizeUriTemplate();

        this.callbackUriSupplier = oauth2CallbackUriSupplier;
        logger.info("authorizeUriTemplate: {}", authorizeUriTemplate);
    }

    /**
     * 接收授权码，并登录。
     * 该uri 要作为 redirect_uri 注册到 oauth2 服务中
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.2">Authorization Code Response</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.2.1">Error Response</a>
     */
    void handleAuthorizationCodeResponse(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String state
    ) throws IOException {
        if (!validateState(request, response, state)) {
            return;
        }
        String error = request.getParameter("error");
        if (Strings.isNotBlank(error)) {
            // OPTIONAL
            String errorDescription = request.getParameter("error_description");
            // OPTIONAL
            String errorUri = request.getParameter("error_uri");
            response.setStatus(403);
            response.getWriter().write("error occur when got authorization code, error: " + error + " error_description: " + errorDescription + ", error_uri: " + errorUri);
        }

        String code = request.getParameter("code");
        if (Strings.isNotBlank(code)) {
            String callbackUri = buildOAuth2CallbackUri(request);
            logger.info("built callback uri for access token request is: {}", callbackUri);
            OAuth2Token oAuth2Token = oauth2ApiService.authorizeToken(
                    oauth2Properties.getClientId(),
                    oauth2Properties.getClientSecret(),
                    code,
                    callbackUri);
            if (oAuth2Token == null) {
                response.setStatus(403);
                response.getWriter().write("Got oauth2 token by authorization_code is null");
                return;
            }
            tokenCache.put(oAuth2Token.getAccessToken(), new OAuth2TokenCachedValue(oAuth2Token));
            logger.info("Got oauth2 token by authorization_code is {}", oAuth2Token);
            validateAccessTokenAndExtractUserInfo(request, response, oAuth2Token.getAccessToken(), false);

            response.addCookie(new Cookie(oauth2Properties.getAccessTokenCookieName(), oAuth2Token.getAccessToken()));

            String redirectUri = getRedirectUriAfterAuthorized(request, callbackUri);
            response.sendRedirect(redirectUri);
        } else {
            response.setStatus(403);
            response.getWriter().write("Got authorization code is null");
        }
    }

    private String getRedirectUriAfterAuthorized(HttpServletRequest request, String callbackUriOfResourceServer) {
        // 优先取 /oauth2/callback?redirect_uri 参数
        String redirectUri = request.getParameter("redirect_uri");
        if (Strings.isBlank(redirectUri)) {
            // 从 session 中获取
            redirectUri = (String) request.getSession().getAttribute(SESSION_KEY_OAUTH2_ORIGINAL_REQUEST);
        }
        if (Strings.isBlank(redirectUri)) {
            // ResourceServer的首页
            redirectUri = oauth2Properties.getHomeUri();
        }
        if (redirectUri == null) {
            redirectUri = "";
        }
        if (Strings.startsWith(redirectUri, "http://") || Strings.startsWith(redirectUri, "https://")) {
            return redirectUri;
        }
        int index = callbackUriOfResourceServer.indexOf("?");
        if (index > 0) {
            callbackUriOfResourceServer = callbackUriOfResourceServer.substring(0, index);
        }

        String callbackPath = oauth2Properties.getCallbackUri();
        if (Strings.startsWith(callbackPath, "http://") || Strings.startsWith(callbackPath, "https://")) {
            return redirectUri;
        }
        redirectUri = callbackUriOfResourceServer.replace(oauth2Properties.getCallbackUri(), redirectUri);
        return redirectUri;
    }

    /**
     * 在收到 Authorization Code时，要先验证 state
     *
     * @param state 状态码
     * @return 返回验证结果
     */
    private boolean validateState(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String state) throws IOException {
        String stateInSession = (String) request.getSession().getAttribute(SESSION_KEY_OAUTH2_CODE_STATE);
        request.getSession().removeAttribute(SESSION_KEY_OAUTH2_CODE_STATE); // state is only used once
        if (oAuth2StateValidator.validate(stateInSession, state)) {
            return true;
        }
        response.setStatus(403);
        response.getWriter().write("invalid state, may be it is an CSRF attack");
        return false;
    }

    private String createState(HttpServletRequest request) {
        HttpSession session = request.getSession();

        String csrfToken = new UUIDv7Generator().get();
        session.setAttribute(SESSION_KEY_OAUTH2_CODE_STATE, csrfToken);
        return csrfToken;
    }

    private void redirectToAuthorizeUri(HttpServletRequest request,
                                        HttpServletResponse response
    ) throws IOException {
        String callbackUri = buildOAuth2CallbackUri(request);
        logger.info("the callback uri was provided to oauth2 server is {}", callbackUri);


        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("client_id", oauth2Properties.getClientId());
        uriVariables.put("state", createState(request));
        uriVariables.put("redirect_uri", callbackUri);
        uriVariables.put("scope", Strings.join(" ", oauth2Properties.getAuthorizeUriScopes()));

        String authorizeUri = UriComponentsBuilder.fromUriString(this.authorizeUriTemplate)
                .uriVariables(uriVariables)
                .enableEncode(Charset.forName(oauth2Properties.getAuthorizeUriEncoding()))
                .build()
                .toUriString();
        logger.info("redirect to oauth server authorize uri: {}", authorizeUri);
        response.sendRedirect(authorizeUri);
    }


    boolean validateAccessTokenAndExtractUserInfo(
            HttpServletRequest request,
            HttpServletResponse response,
            String accessToken,
            boolean recordOriginalRequestIfUnauthorized) throws IOException {

        try {
            IntrospectResult introspectResult = validateOrRefreshAccessToken(accessToken);
            Userinfo userInfo = getUserInfo(accessToken, introspectResult);
            request.setAttribute(REQUEST_KEY_OAUTH2_AUTHORIZED_USER, userInfo);
            return true;
        } catch (InvalidAccessTokenException e) {
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String originalRequest = uri;
            if (Strings.isNotBlank(queryString)) {
                originalRequest = uri + "?" + queryString;
            }
            if (recordOriginalRequestIfUnauthorized) {
                logger.info("record original request uri: {}", originalRequest);
                request.getSession().setAttribute(OAuth2AuthzHandler.SESSION_KEY_OAUTH2_ORIGINAL_REQUEST, originalRequest);
            }
            logger.info("access token is invalid, error: {}, uri: {}", e.getMessage(), originalRequest);
            redirectToAuthorizeUri(request, response);
            return false;
        }
    }

    private Userinfo getUserInfo(String accessToken,
                                 @Nullable IntrospectResult introspectResult) {

        Userinfo userInfo = null;
        if (oauth2Properties.isExtractUserinfoWithIdToken()) {

            OpenIdToken idToken = null;
            OAuth2TokenCachedValue oAuth2TokenCachedValue = tokenCache.get(accessToken);
            OAuth2Token oAuth2Token = oAuth2TokenCachedValue == null ? null : oAuth2TokenCachedValue.getOauth2Token();
            if (oAuth2Token != null) {
                idToken = oAuth2Token.getIdTokenObject();
                if (idToken == null) {
                    String idTokenString = oAuth2Token.getIdToken();
                    if (Strings.isNotBlank(idTokenString)) {
                        try {
                            idToken = openIdTokenParser.parse(idTokenString);
                            oAuth2Token.setIdTokenObject(idToken);
                        } catch (Exception e) {
                            logger.error("parse id token error", e);
                        }
                    }
                }
            }

            if (idToken != null) {
                try {
                    userInfo = openIdTokenUserInfoExtractor.extract(idToken);
                    return userInfo;
                } catch (Exception e) {
                    logger.error("extract user info with idToken error", e);
                }
            }
        }

        if (oauth2Properties.isExtractUserinfoWithIntrospectResult()) {
            if (introspectResult != null) {
                try {
                    userInfo = introspectResultUserInfoExtractor.extract(introspectResult);
                    return userInfo;
                } catch (Exception e) {
                    logger.error("extract user info with introspectResult error", e);
                }
            }
        }

        userInfo = oauth2ApiService.userInfo(accessToken);
        return userInfo;
    }


    /**
     * 验证 access_token
     * <ul>
     *     <li>bearer类型的token，会调用 bearerAccessTokenValidator 进行验证</li>
     *     <li>introspect类型的token，会调用 introspectAccessTokenValidator 进行验证</li>
     * </ul>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-7">Accessing Protected Resources</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6750">Access token 格式规范</a>
     */
    private IntrospectResult validateOrRefreshAccessToken(String accessToken) {
        IntrospectResult result = null;
        try {
            result = validateAccessTokenInternal(accessToken);
        } catch (ExpiredAccessTokenException e) {
            logger.warn("access-token expired, will refresh it");
            OAuth2TokenCachedValue oAuth2TokenCachedValue = tokenCache.get(accessToken);
            OAuth2Token oAuth2Token = oAuth2TokenCachedValue == null ? null : oAuth2TokenCachedValue.getOauth2Token();
            String refreshToken = oAuth2Token == null ? null : oAuth2Token.getRefreshToken();
            tokenCache.remove(accessToken);
            if (Strings.isBlank(refreshToken)) {
                logger.info("the refresh token was not found");
                throw new ExpiredAccessTokenException("access-token expired, it's refresh token was not found");
            }
            logger.info("the refresh token was found, will refresh it");
            try {
                oAuth2Token = oauth2ApiService.refreshToken(oauth2Properties.getClientId(), oauth2Properties.getClientSecret(), refreshToken);
            } catch (Throwable ex) {
                logger.info("refresh token failed, will redirect to authorize uri. error: {}", ex.getMessage(), ex);
                throw new ExpiredAccessTokenException("access-token expired, refresh failed: " + ex.getMessage());
            }
            tokenCache.put(oAuth2Token.getAccessToken(), new OAuth2TokenCachedValue(oAuth2Token));
            try {
                result = validateAccessTokenInternal(oAuth2Token.getAccessToken());
            } catch (InvalidAccessTokenException ex2) {
                logger.warn("validate refreshed access token failed: {}", ex2.getMessage(), ex2);
            }
        }

        return result;
    }


    /**
     * 验证access_token,
     * 要么返回 null，要么返回 IntrospectResult
     */
    private IntrospectResult validateAccessTokenInternal(String accessToken) throws InvalidAccessTokenException {
        if (Strings.isBlank(accessToken)) {
            throw new InvalidAccessTokenException("access token is missing");
        }
        OAuth2TokenCachedValue oAuth2TokenCachedValue = tokenCache.get(accessToken);
        OAuth2Token oAuth2Token = oAuth2TokenCachedValue == null ? null : oAuth2TokenCachedValue.getOauth2Token();
        String tokenType = null;
        if (oAuth2Token != null) {
            tokenType = oAuth2Token.getTokenType();
            oAuth2TokenValidator.validate(oAuth2TokenCachedValue);
        } else {
            logger.info("the oauth2 token is not found for access-token {}", accessToken);
        }

        JWT jwt = null;
        if (Strings.isBlank(tokenType)) {
            try {
                jwt = JWTs.parse(accessToken);
                tokenType = "bearer";
            } catch (JWTException e) {
                logger.info("access token is not jwt format: {}", accessToken);
            }
        }

        if (Strings.equalsIgnoreCase("bearer", tokenType)) {
            if (jwt == null) {
                try {
                    jwt = JWTs.parse(accessToken);
                } catch (JWTException e) {
                    throw new InvalidAccessTokenException("illegal bearer type access token, it is not jwt format");
                }
            }
            bearerAccessTokenValidator.validate(jwt);
            return null;
        } else {
            if (oauth2ApiService.tokenIntrospectionEndpointEnabled()) {
                IntrospectResult introspectResult = oauth2ApiService.introspect(accessToken, "access_token");
                introspectAccessTokenValidator.validate(introspectResult);
                return introspectResult;
            } else {
                throw new IllegalStateException("validate access token failed, the access token is not a bearer token , and the token introspection is disabled or unsupported");
            }
        }
    }

    /**
     * 创建当前 Resource Server 提供的 callback-uri
     */
    private String buildOAuth2CallbackUri(HttpServletRequest request) {
        final String path = oauth2Properties.getCallbackUri();
        String callbackUri = path;
        if (Strings.startsWith(callbackUri, "http://") || Strings.startsWith(callbackUri, "https://")) {
            return callbackUri;
        }
        callbackUri = callbackUriSupplier.get(path, request);
        logger.info("the callback uri is {}", callbackUri);
        return callbackUri;
    }


}
