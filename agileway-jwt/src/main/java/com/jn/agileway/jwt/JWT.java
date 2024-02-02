package com.jn.agileway.jwt;


public interface JWT {
    Header getHeader();
    void setHeader(Header header) ;

    Payload getPayload();

    void setPayload(Payload payload) ;

    String toToken();


}
