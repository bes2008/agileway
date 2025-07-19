package com.jn.agileway.jwt;

/**
 * 验证的目的是防止篡改，防止伪造
 */
public interface Verifier {
    boolean verify(JWSToken token);
}
