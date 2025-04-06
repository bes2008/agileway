package com.jn.agileway.http.authc.digest;

import com.jn.agileway.http.authc.AuthorizationHeaderBuilder;
import com.jn.agileway.http.authc.UserPasswordCredentials;
import com.jn.agileway.http.authc.WwwAuthenticate;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.codec.StringifyFormat;
import com.jn.langx.codec.Stringifys;
import com.jn.langx.security.crypto.digest.MessageDigests;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.random.Randoms;
import com.jn.langx.validation.rule.CharData;

public class DigestAuthorizationHeaderBuilder extends AuthorizationHeaderBuilder<DigestAuthorizationHeaderBuilder, DigestWwwAuthenticate, UserPasswordCredentials> {
    @NotEmpty
    private String requestUri;
    private String requestMethod;
    private String requestEntityBody;

    public DigestAuthorizationHeaderBuilder() {
        super();
    }

    public DigestAuthorizationHeaderBuilder withRequestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }

    public DigestAuthorizationHeaderBuilder withRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public DigestAuthorizationHeaderBuilder withRequestEntityBody(String requestEntityBody) {
        this.requestEntityBody = requestEntityBody;
        return this;
    }

    @Override
    public String build() {

        String username = this.credentials.getUsername();
        String password = this.credentials.getPassword();
        String realm = this.wwwAuthenticate.getRealm();
        String digestAlgorithm = this.wwwAuthenticate.getAlgorithm().getName();
        String nonce = this.wwwAuthenticate.getNonce();
        String cnonce = generateCNonce();

        String A1 = unq(username) + ":" + unq(realm) + ":" + password;
        if (Strings.endsWith(digestAlgorithm, "-sess")) {
            A1 = hash(A1) + ":" + unq(nonce) + ":" + unq(cnonce);
        }
        String qop = this.wwwAuthenticate.getQop();
        String A2 = requestMethod + ":" + requestUri;
        if ("auth-int".equals(qop)) {
            A2 = A2 + ":" + hash(requestEntityBody);
        }
        String usernameHash = username;
        if (this.wwwAuthenticate.isUserhash()) {
            usernameHash = hash(unq(username) + ":" + unq(realm));
        }
        String nc = "00000001";
        String response = kd(hash(A1), StringTemplates.formatWithPlaceholder("{}:{}:{}:{}:{}", unq(nonce), nc, unq(cnonce), unq(qop), hash(A2)));

        String template = "Digest username={}, realm=\"{}\", uri={}, algorithm={}, nonce=\"{}\", nc={}, cnonce=\"{}\", qop={}, response=\"{}\", opaque=\"{}\", userhash={}";
        String result = StringTemplates.formatWithPlaceholder(template, usernameHash, realm, requestUri, digestAlgorithm, nonce, nc, cnonce, qop, response, this.wwwAuthenticate.getOpaque(), this.wwwAuthenticate.isUserhash());
        return result;
    }

    /**
     * H(data)
     */
    private String hash(String data) {
        String digestAlgorithm = this.wwwAuthenticate.getAlgorithm().getName();
        if (Strings.endsWith(digestAlgorithm, "-sess")) {
            digestAlgorithm = Strings.substring(digestAlgorithm, 0, digestAlgorithm.length() - 5);
        }
        return MessageDigests.digestToString(digestAlgorithm, data.getBytes(), null, 1, StringifyFormat.HEX);
    }

    private String generateCNonce() {
        String clientNonce = Randoms.randomString(CharData.ALPHABET_DIGITS.getChars(), 16);
        return Stringifys.stringify(clientNonce.getBytes(Charsets.UTF_8), StringifyFormat.BASE64);
    }

    /**
     * KD(secret, data)
     */
    private String kd(String secret, String data) {
        return hash(secret + ":" + data);
    }

    private static String unq(String str) {
        return WwwAuthenticate.unquoted(str);
    }


}
