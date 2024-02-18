package com.jn.agileway.jwt.tests;

import com.jn.agileway.jwt.HMacVerifier;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTs;
import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.JCAEStandardName;
import com.jn.langx.security.crypto.key.PKIs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                System.out.println(StringTemplates.formatWithPlaceholder("algorithm `{}`,  type is: {}, jwt object create success: {}", algorithm, JWTs.getJWTService().getAlgorithmType(algorithm)));
            }
        });

    }

    @Test
    public void testJWTPlainToken() throws Exception{
        // 创建 plain token
        Map<String,Object> payload= Maps.newHashMap();
        payload.put("hello","world");
        payload.put("abc",123);
        final JWT token0 = JWTs.newJWTPlainToken(null, payload);

        String tokenString=token0.toUtf8UrlEncodedToken();

        System.out.println(tokenString);


        // 使用agileway-jwt 的解析器进行解析
        final JWT token1=JWTs.parse(tokenString);

        // 使用jose 方式来解析
        final com.nimbusds.jwt.JWT token2=JWTParser.parse(tokenString);

        // 对header进行比较
        Set<String> headerKeys=token2.getHeader().getIncludedParams();
        Collects.forEach(headerKeys, new Consumer<String>() {
            @Override
            public void accept(String key) {
                Object valueInToken2=token2.getHeader().getCustomParam(key);
                Object valueInToken1=token1.getHeader().get(key);
                Object valueInToken0=token0.getHeader().get(key);

                String json0= JSONs.toJson(valueInToken0);
                String json1= JSONs.toJson(valueInToken1);
                String json2= JSONs.toJson(valueInToken2);

                if(Objs.equals(key, JWTs.Headers.ALGORITHM ) || Objs.equals(key,JWTs.Headers.TYPE)){
                    System.out.println(Objs.deepEquals(json0, json1) );
                }else{
                    System.out.println(Objs.deepEquals(json0, valueInToken1) && Objs.deepEquals(json0, json2));
                }

            }
        });

        // 对payload进行比较
        Set<String> payloadKeys=token2.getJWTClaimsSet().getClaims().keySet();
        Collects.forEach(payloadKeys, new Consumer<String>() {
            @Override
            public void accept(String key) {
                Object valueInToken2= null;
                try {
                    valueInToken2 = token2.getJWTClaimsSet().getClaim(key);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Object valueInToken1=token1.getPayload().get(key);
                Object valueInToken0=token0.getPayload().get(key);

                String json0= JSONs.toJson(valueInToken0);
                String json1= JSONs.toJson(valueInToken1);
                String json2= JSONs.toJson(valueInToken2);
                System.out.println(Objs.deepEquals(json0, json1) && Objs.deepEquals(json0, json2));
            }
        });

    }

    @Test
    public void testJWSToken_HS256() throws Exception{
        // 创建 secret key
        SecretKey secretKey = PKIs.createSecretKey(JCAEStandardName.AES.getName(),null,256, Securitys.getSecureRandom());

        // 创建 plain token
        Map<String,Object> payload= Maps.newHashMap();
        payload.put("hello","world");
        payload.put("abc",123);


        final JWT token0 = JWTs.newJWTSignedToken(JWTs.JWSAlgorithms.HS256,null, payload, secretKey);

        String tokenString=token0.toUtf8UrlEncodedToken();

        System.out.println(tokenString);


        // 使用agileway-jwt 的解析器进行解析
        final JWT token1=JWTs.parse(tokenString);

        // 使用jose 方式来解析
        final com.nimbusds.jwt.JWT token2=JWTParser.parse(tokenString);

        // 对header进行比较
        Set<String> headerKeys=token2.getHeader().getIncludedParams();
        Collects.forEach(headerKeys, new Consumer<String>() {
            @Override
            public void accept(String key) {
                Object valueInToken2=token2.getHeader().getCustomParam(key);
                Object valueInToken1=token1.getHeader().get(key);
                Object valueInToken0=token0.getHeader().get(key);

                String json0= JSONs.toJson(valueInToken0);
                String json1= JSONs.toJson(valueInToken1);
                String json2= JSONs.toJson(valueInToken2);

                if(Objs.equals(key, JWTs.Headers.ALGORITHM ) || Objs.equals(key,JWTs.Headers.TYPE)){
                    System.out.println(Objs.deepEquals(json0, json1) );
                }else{
                    System.out.println(Objs.deepEquals(json0, valueInToken1) && Objs.deepEquals(json0, json2));
                }

            }
        });

        // 对payload进行比较
        Set<String> payloadKeys=token2.getJWTClaimsSet().getClaims().keySet();
        Collects.forEach(payloadKeys, new Consumer<String>() {
            @Override
            public void accept(String key) {
                Object valueInToken2= null;
                try {
                    valueInToken2 = token2.getJWTClaimsSet().getClaim(key);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Object valueInToken1=token1.getPayload().get(key);
                Object valueInToken0=token0.getPayload().get(key);

                String json0= JSONs.toJson(valueInToken0);
                String json1= JSONs.toJson(valueInToken1);
                String json2= JSONs.toJson(valueInToken2);
                System.out.println(Objs.deepEquals(json0, json1) && Objs.deepEquals(json0, json2));
            }
        });

        // 进行验证
        System.out.println("token0 verify: "+new HMacVerifier(secretKey).verify((JWSToken) token0));
        System.out.println("token1 verify: "+new HMacVerifier(secretKey).verify((JWSToken) token1));

        System.out.println("token2 verify: "+ ((SignedJWT)token2).verify(new MACVerifier(secretKey)) );

    }
}
