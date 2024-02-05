package com.jn.agileway.jwt;
import java.util.List;
public interface JWEPlugin extends JWTParser<JWEToken>{
    List<String> getSupportedJWEAlgorithms();
    JWEToken parse(String jwe);
}
