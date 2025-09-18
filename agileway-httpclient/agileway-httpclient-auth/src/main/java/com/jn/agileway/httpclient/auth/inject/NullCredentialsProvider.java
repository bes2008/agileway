package com.jn.agileway.httpclient.auth.inject;

import com.jn.agileway.httpclient.auth.Credentials;
import com.jn.agileway.httpclient.auth.CredentialsProvider;

public class NullCredentialsProvider implements CredentialsProvider {
    @Override
    public Credentials get(Object httpRequest) {
        return null;
    }
}
