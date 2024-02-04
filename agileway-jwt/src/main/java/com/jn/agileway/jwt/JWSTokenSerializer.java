package com.jn.agileway.jwt;

public interface JWSTokenSerializer extends JWTSerializer<JWSToken>{
    @Override
    String serialize(JWSToken jwt);
}
