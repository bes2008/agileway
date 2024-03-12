package com.jn.agileway.jwt.tests.nacos_jwt;

import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.JWSTokenBuilder;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.key.PKIs;
import org.junit.Test;

import javax.crypto.SecretKey;

public class NacosJwtTests {

    @Test
    public void generateJWTString(){
        JWSTokenBuilder builder = new JWSTokenBuilder(false);

        SecretKey secretKey= PKIs.createSecretKey(JWTs.getJcaHMacAlgorithm(JWTs.JWSAlgorithms.HS256),Base64.decodeBase64("cAbSh5EoQMUIoDPN5qWq7GPE0mKwKMJIAnT6E2O9uV0="));


        JWSToken token = builder.withHeaderClaim("alg",JWTs.JWSAlgorithms.HS256)
                .withPayloadClaim("sub","nacos")
                .withPayloadClaim("exp","1800000000")
                .sign(secretKey);

        String jwt = token.toUtf8UrlEncodedToken();
        System.out.println(jwt);

        JWT t=JWTs.getJWTService().newParser().parse(jwt);
        System.out.println(t);

    }

    @Test
    public void parseJWT(){
        NacosUser nacosUser= new NacosJwtParser("cAbSh5EoQMUIoDPN5qWq7GPE0mKwKMJIAnT6E2O9uV0=").parse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuYWNvcyIsImV4cCI6IjE4MDAwMDAwMDAifQ.OV6OaSPL7xKUQ4dZA0z46whoGp0o4ErMXvgipietGho");
    }


    @Test
    public void parseHeader(){
        System.out.println(Base64.decodeBase64ToString("eyJhbGciOiJIUzI1NiJ9"));
    }
}
