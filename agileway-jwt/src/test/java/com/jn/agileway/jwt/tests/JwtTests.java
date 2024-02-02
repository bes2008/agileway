package com.jn.agileway.jwt.tests;

import com.jn.agileway.jwt.JWTs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import org.junit.Test;

import java.util.List;

public class JwtTests {
    @Test
    public void showSupportedAlgorithms(){
        List<String> jwsAlgorithms= JWTs.getJWTService().supportedJWSAlgorithms();
        System.out.println(StringTemplates.formatWithPlaceholder("jws algorithms: {}", Strings.join(",",jwsAlgorithms)));
        List<String> jweAlgorithms= JWTs.getJWTService().supportedJWEAlgorithms();
        System.out.println(StringTemplates.formatWithPlaceholder("jwe algorithms: {}", Strings.join(",",jweAlgorithms)));

        List<String> testsAlgorithms= Lists.newArrayList("None",jweAlgorithms.get(0),jwsAlgorithms.get(0),"hello");

        Pipeline.of(testsAlgorithms).forEach(new Consumer<String>() {
            @Override
            public void accept(String algorithm) {
                System.out.println(StringTemplates.formatWithPlaceholder("algorith `{}`,  type is: {}", algorithm, JWTs.getJWTService().getAlgorithmType(algorithm)));
            }
        });

    }
}
