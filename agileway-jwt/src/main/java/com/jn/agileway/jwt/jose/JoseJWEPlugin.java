package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.jwe.JWEPlugin;
import com.jn.agileway.jwt.jwe.JWEToken;
import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.jwe.JWETokenBuilder;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.comparator.Comparators;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jwt.EncryptedJWT;

import java.lang.reflect.Field;
import java.util.List;

public class JoseJWEPlugin implements JWEPlugin {


    private Holder<List<String>> jweAlgorithms=new Holder<List<String>>();

    @Override
    public JWETokenBuilder newJWEBuilder() {
        return null;
    }

    @Override
    public List<String> getSupportedJWEAlgorithms() {
        if(jweAlgorithms.isNull()) {
            List<String> algorithms= Pipeline.of(Reflects.getAllDeclaredFields(JWEAlgorithm.class, true))
                    .filter(new Predicate<Field>() {
                        @Override
                        public boolean test(Field field) {
                            if(!Modifiers.isStatic(field)){
                                return false;
                            }
                            if(field.getType()!= JWEAlgorithm.class){
                                return false;
                            }
                            return true;
                        }
                    }).map(new Function<Field, String>() {
                        @Override
                        public String apply(Field field) {
                            JWEAlgorithm algorithm= Reflects.getFieldValue(field, JWEAlgorithm.class, true, true);
                            return algorithm.getName();
                        }
                    })
                    .sort(Comparators.STRING_COMPARATOR)
                    .asList();
            jweAlgorithms.set(algorithms);
        }
        return Lists.immutableList(jweAlgorithms.get());
    }

    @Override
    public JWEToken parse(String jwtstring) {
        try {
            EncryptedJWT jwt = EncryptedJWT.parse(jwtstring);
            return new JoseJwtEncryptedToken((EncryptedJWT) jwt);
        }catch (Throwable e){
            throw new JWTException("invalid jwt encrypted token, "+e.getMessage(),e);
        }
    }
}
