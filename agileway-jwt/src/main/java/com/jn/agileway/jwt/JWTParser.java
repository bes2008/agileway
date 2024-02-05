package com.jn.agileway.jwt;

import com.jn.langx.Parser;

public interface JWTParser<T extends JWT> extends Parser<String, T> {
    @Override
    T parse(String jwtstring);
}
