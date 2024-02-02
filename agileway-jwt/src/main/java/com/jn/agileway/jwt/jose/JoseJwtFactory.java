package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTFactory;
import com.jn.agileway.jwt.AlgorithmType;

public class JoseJwtFactory<T extends JWT> implements JWTFactory<T> {

    @Override
    public T get(String algorithm) {
        JWT jwt =null;
        AlgorithmType algorithmType = JoseJWTService.INSTANCE.getAlgorithmType(algorithm);
        switch (algorithmType){
            case NONE:
                jwt= new JoseJwtAdapter();
                break;
            case JWS:
                jwt = new JoseJwtSignedTokenAdapter();
                break;
            case JWE:
                jwt =new JoseJwtEncryptedTokenAdapter();
                break;
            case UNSUPPORTED:
                throw new RuntimeException("Unsupported JWT algorithm: "+algorithm);
        }
        return (T)jwt;
    }

}
