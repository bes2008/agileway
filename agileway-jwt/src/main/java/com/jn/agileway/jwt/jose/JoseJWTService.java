package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.spi.AbstractJWTService;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.Sets;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

import java.lang.reflect.Field;
import java.util.Set;

public class JoseJWTService extends AbstractJWTService {
    private Holder<Set<String>> jwsAlgorithms=new Holder<Set<String>>();
    private Holder<Set<String>> jweAlgorithms=new Holder<Set<String>>();


    @Override
    public Set<String> supportedJWSAlgorithms() {
        if(jwsAlgorithms.isNull()) {
            Set<String> algorithms= Pipeline.of(Reflects.getAllDeclaredFields(JWSAlgorithm.class, true))
                    .filter(new Predicate<Field>() {
                        @Override
                        public boolean test(Field field) {
                            if(!Modifiers.isStatic(field)){
                                return false;
                            }
                            if(field.getType()!= JWSAlgorithm.class){
                                return false;
                            }
                            return true;
                        }
                    }).map(new Function<Field, String>() {
                        @Override
                        public String apply(Field field) {
                            JWSAlgorithm algorithm= Reflects.getFieldValue(field, JWSAlgorithm.class, true, true);
                            return algorithm.getName();
                        }
                    }).asSet(true);
            jwsAlgorithms.set(algorithms);
        }
        return Sets.immutableSet(jwsAlgorithms.get());
    }

    @Override
    public Set<String> supportedJWEAlgorithms() {
        if(jweAlgorithms.isNull()) {
            Set<String> algorithms= Pipeline.of(Reflects.getAllDeclaredFields(JWEAlgorithm.class, true))
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
                    }).asSet(true);
            jweAlgorithms.set(algorithms);
        }
        return Sets.immutableSet(jweAlgorithms.get());
    }

}
