package com.jn.agileway.jwt.jwe;
import com.jn.agileway.jwt.JWTParser;

import java.util.List;
public interface JWEPlugin extends JWTParser<JWEToken> {
    List<String> getSupportedJWEAlgorithms();
    JWEToken parse(String jwe);

    JWETokenBuilder newJWEBuilder();
}
