package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.AbstractJWTService;
import com.jn.agileway.jwt.JWTFactory;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.comparator.Comparators;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

import java.lang.reflect.Field;
import java.util.List;

public class JoseJWTService extends AbstractJWTService {

    public static final JoseJWTService INSTANCE= new JoseJWTService();
    private Holder<List<String>> jwsAlgorithms=new Holder<List<String>>();
    private Holder<List<String>> jweAlgorithms=new Holder<List<String>>();


    @Override
    public List<String> supportedJWSAlgorithms() {
        if(jwsAlgorithms.isNull()) {
            List<String> algorithms= Pipeline.of(Reflects.getAllDeclaredFields(JWSAlgorithm.class, true))
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
                    })
                    .sort(Comparators.STRING_COMPARATOR)
                    .asList();
            jwsAlgorithms.set(algorithms);
        }
        return Lists.immutableList(jwsAlgorithms.get());
    }

    @Override
    public List<String> supportedJWEAlgorithms() {
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

    private JoseJwtFactory factory = new JoseJwtFactory();
    @Override
    public JWTFactory getJWTFactory() {
        return factory;
    }
}
