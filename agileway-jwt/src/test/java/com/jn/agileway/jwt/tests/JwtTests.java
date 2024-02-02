package com.jn.agileway.jwt.tests;

import com.jn.agileway.jwt.JWTServiceProvider;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import org.junit.Test;

public class JwtTests {
    @Test
    public void showSupportedAlgorithms(){
        System.out.println(StringTemplates.formatWithPlaceholder("jws algorithms: {}", Strings.join(",",JWTServiceProvider.getJWTService().supportedJWSAlgorithms())));
        System.out.println(StringTemplates.formatWithPlaceholder("jwe algorithms: {}", Strings.join(",",JWTServiceProvider.getJWTService().supportedJWEAlgorithms())));
    }
}
