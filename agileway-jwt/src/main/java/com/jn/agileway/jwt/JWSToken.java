package com.jn.agileway.jwt;

public interface JWSToken extends JWT {
    /**
     *
     * @return 获取签名 utf8 url 格式
     */
    String getBase64UrlEncodedSignature();
}
