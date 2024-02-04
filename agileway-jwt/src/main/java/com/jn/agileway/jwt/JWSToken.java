package com.jn.agileway.jwt;

import java.util.Map;

public class JWSToken extends JWTPlainToken {
    public JWSToken(Header header, Payload payload) {
        super(header, payload);
    }

    public JWSToken(Map<String, Object> header, Map<String, Object> payload) {
        super(header, payload);
    }
}
