package com.jn.agileway.http.authc;

public enum AuthScheme {
    BASIC("Basic"),
    DIGEST("Digest"),
    NTLM("NTLM"),
    SPNEGO("SPNEGO"),
    KERBEROS("Kerberos"),
    NEGOTIATE("Negotiate"),
    BEARER("Bearer");

    private String scheme;

    AuthScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }
}
