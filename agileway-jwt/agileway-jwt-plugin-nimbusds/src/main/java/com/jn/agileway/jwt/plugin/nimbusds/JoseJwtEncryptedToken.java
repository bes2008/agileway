package com.jn.agileway.jwt.plugin.nimbusds;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.Payload;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;

public class JoseJwtEncryptedToken implements JWEToken {
    private EncryptedJWT delegate;
    private Header header;
    private Payload payload;
    public JoseJwtEncryptedToken() {

    }

    public JoseJwtEncryptedToken(EncryptedJWT delegate) {
        this.delegate = delegate;
    }

    @Override
    public Header getHeader() {
        if (header == null) {
            JWEHeader h = delegate.getHeader();
            this.header = new Header(h.toJSONObject());
        }
        return header;
    }


    @Override
    public Payload getPayload() {
        if (payload == null) {
            this.payload = new Payload(delegate.getPayload().toJSONObject());
        }
        return payload;
    }

    @Override
    public String toUtf8UrlEncodedToken() {
        return null;
    }
}
