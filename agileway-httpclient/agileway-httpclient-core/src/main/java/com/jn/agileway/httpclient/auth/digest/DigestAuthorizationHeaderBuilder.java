package com.jn.agileway.httpclient.auth.digest;

import com.jn.agileway.httpclient.auth.AuthorizationHeaderBuilder;
import com.jn.agileway.httpclient.auth.UserPasswordCredentials;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.codec.StringifyFormat;
import com.jn.langx.codec.Stringifys;
import com.jn.langx.security.crypto.digest.MessageDigests;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.http.HttpHeaderValueBuilders;
import com.jn.langx.util.random.Randoms;
import com.jn.langx.validation.rule.CharData;

import java.util.List;
import java.util.Map;

import static com.jn.langx.util.Strings.quoted;
import static com.jn.langx.util.Strings.unquoted;

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

        String A1 = unquoted(username) + ":" + unquoted(realm) + ":" + password;
        if (Strings.endsWith(digestAlgorithm, "-sess")) {
            A1 = hash(A1) + ":" + unquoted(nonce) + ":" + unquoted(cnonce);
        }
        List<String> qops = this.wwwAuthenticate.getQopAsList();
        String A2 = requestMethod + ":" + requestUri;
        String qop = "auth";
        if (qops.contains("auth-int")) {
            qop = "auth-int";
            A2 = A2 + ":" + hash(requestEntityBody);
        }
        String usernameHash = username;
        if (this.wwwAuthenticate.isUserhash()) {
            usernameHash = hash(unquoted(username) + ":" + unquoted(realm));
        }
        String nc = "00000001";
        String response = kd(hash(A1), StringTemplates.formatWithPlaceholder("{}:{}:{}:{}:{}", unquoted(nonce), nc, unquoted(cnonce), unquoted(qop), hash(A2)));

        Map<String, String> fields = this.wwwAuthenticate.getFields();
        fields.put("username", quoted(usernameHash));
        fields.put("realm", quoted(realm));
        fields.put("uri", quoted(requestUri));
        fields.put("algorithm", unquoted(digestAlgorithm));
        fields.put("nonce", quoted(nonce));
        fields.put("cnonce", quoted(cnonce));
        fields.put("nc", unquoted(nc));
        fields.put("qop", unquoted(qop));
        fields.put("response", quoted(response));
        fields.put("opaque", quoted(this.wwwAuthenticate.getOpaque()));
        fields.put("userhash", "" + this.wwwAuthenticate.isUserhash());

        String template = HttpHeaderValueBuilders.buildHeaderValueWithType("Digest", " ", fields, ", ");
        return template;
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


}
