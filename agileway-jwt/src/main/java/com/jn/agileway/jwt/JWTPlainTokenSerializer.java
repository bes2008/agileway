package com.jn.agileway.jwt;

public interface JWTPlainTokenSerializer extends JWTSerializer<JWTPlainToken> {
    @Override
    String serialize(JWTPlainToken jwt) ;
}
