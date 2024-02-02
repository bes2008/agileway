package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTFactory;
import com.jn.agileway.jwt.AlgorithmType;

public class JoseJwtFactory<T extends JWT> implements JWTFactory<T> {

    @Override
    public T get(String algorithm) {
        T jwt =null;
        AlgorithmType algorithmType = JoseJWTService.INSTANCE.getAlgorithmType(algorithm);
        switch (algorithmType){
            case NONE:
                jwt=(T) new JoseJwtAdapter();
                break;
            case JWS:
                jwt =(T) new JoseJwtSignedTokenAdapter();
                break;
            case JWE:
                jwt =(T)new JoseJwtEncryptedTokenAdapter();
                break;
            case UNSUPPORTED:
                break;
        }
        return jwt;
    }

}
