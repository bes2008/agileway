package com.jn.agileway.jwt;

public interface Signer {
    String sign(Header header, Payload payload);
}
