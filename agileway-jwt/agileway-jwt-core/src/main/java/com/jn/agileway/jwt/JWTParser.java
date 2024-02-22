package com.jn.agileway.jwt;

import com.jn.langx.Parser;

public interface JWTParser extends Parser<String, JWT> {
    @Override
    JWT parse(String jwtstring);
}
