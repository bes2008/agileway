package com.jn.agileway.jwt;

public interface JWTCodec{
    // JWT => JsonTreeNode => byte[] => enc => Base64URL parts => sign => merge =>  String
    String encode(JWT jwt);

    // String => split  => verify_sign => Base64URL parts = dec  => byte[] => JsonTreeNode => JWT
    JWT decode(String bytes);
}
