package com.jn.agileway.jwt;


public interface JWT {
    Header getHeader();

    Payload getPayload();

    String toUtf8UrlEncodedToken();

}
