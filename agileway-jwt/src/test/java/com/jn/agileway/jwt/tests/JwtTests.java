package com.jn.agileway.jwt.tests;

import com.jn.agileway.jwt.JWTServiceProvider;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import org.junit.Test;

import java.util.List;

public class JwtTests {
    @Test
    public void showSupportedAlgorithms(){
        List<String> jwsAlgorithms= JWTServiceProvider.getJWTService().supportedJWSAlgorithms();
        System.out.println(StringTemplates.formatWithPlaceholder("jws algorithms: {}", Strings.join(",",jwsAlgorithms)));
        List<String> jweAlgorithms= JWTServiceProvider.getJWTService().supportedJWEAlgorithms();
        System.out.println(StringTemplates.formatWithPlaceholder("jwe algorithms: {}", Strings.join(",",jweAlgorithms)));
    }
}
