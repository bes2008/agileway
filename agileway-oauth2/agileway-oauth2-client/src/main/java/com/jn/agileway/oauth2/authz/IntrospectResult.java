package com.jn.agileway.oauth2.authz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntrospectResult {
    /**
     * REQUIRED.  Boolean indicator of whether or not the presented token
     * is currently active.  The specifics of a token's "active" state
     * will vary depending on the implementation of the authorization
     * server and the information it keeps about its tokens, but a "true"
     * value return for the "active" property will generally indicate
     * that a given token has been issued by this authorization server,
     * has not been revoked by the resource owner, and is within its
     * given time window of validity (e.g., after its issuance time and
     * before its expiration time).  See Section 4 for information on
     * implementation of such checks.
     */
    private boolean active;

    /**
     * OPTIONAL.  A JSON string containing a space-separated list of
     * scopes associated with this token, in the format described in
     * Section 3.3 of OAuth 2.0 [RFC6749].
     * <p>
     * <p>
     * token 的授权范围，比如：read write
     */
    private List<String> scope;

    private String clientId;

    private String username;

    private String tokenType;

    /**
     * 过期时间，单位 s
     */
    private Long exp;

    /**
     * oauth2 token  颁发（创建）时间，单位 s
     */
    private Long iat;

    /**
     * oauth2 token  开始使用时间，单位 s
     */
    private Long nbf;

    private String subject;

    private String aud;

    /**
     * oauth2 token 颁发机构
     */
    private String iss;

    private String jti;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public Long getNbf() {
        return nbf;
    }

    public void setNbf(Long nbf) {
        this.nbf = nbf;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    private Map<String, Object> extensionFields = new HashMap<>();

    public Map<String, Object> getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(Map<String, Object> extensionFields) {
        this.extensionFields = extensionFields;
    }
}
