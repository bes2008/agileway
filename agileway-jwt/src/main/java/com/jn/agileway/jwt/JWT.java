package com.jn.agileway.jwt;

import java.util.List;

public interface JWT {
    Header getHeader();
    void setHeader(Header header) ;

    Payload getPayload();

    void setPayload(Payload payload) ;

    String getJWTString();

    List<String> getParts();
}
