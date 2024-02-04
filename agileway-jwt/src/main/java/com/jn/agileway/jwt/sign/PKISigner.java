package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.IllegalJWTException;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;

import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.List;
import java.util.Map;

public class PKISigner implements Signer {

    private static Map<String,Supplier<PrivateKey, Signature>> jwtAlgorithmToPKI;
    static{
        Map<String,Supplier<PrivateKey, Signature>>  map  = Maps.newLinkedHashMap();
        map.put("RS256", new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                Signature signer= Signatures.createSignature("SHA256withRSA",null, privateKey,null);
                return signer;
            }
        });
        map.put("RS384", new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                Signature signer= Signatures.createSignature("SHA384withRSA",null, privateKey,null);
                return signer;
            }
        });
        map.put("RS512", new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                Signature signer= Signatures.createSignature("SHA512withRSA",null, privateKey,null);
                return signer;
            }
        });

        map.put("PS256", new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1);
                Signature signer= Signatures.createSignature("RSASSA-PSS",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("PS384",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1);
                Signature signer= Signatures.createSignature("RSASSA-PSS",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("PS512",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                Signature signer= Signatures.createSignature("RSASSA-PSS",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("ES256",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                Signature signer= Signatures.createSignature("SHA256withECDSA",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("ES256K",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                Signature signer= Signatures.createSignature("SHA256withECDSA",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("ES384",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                Signature signer= Signatures.createSignature("SHA384withECDSA",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        map.put("ES512",new Supplier<PrivateKey, Signature>(){
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec =new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                Signature signer= Signatures.createSignature("SHA512withECDSA",null, privateKey,null, parameterSpec);
                return signer;
            }
        });
        jwtAlgorithmToPKI=map;
    }

    private final PrivateKey privateKey;

    public PKISigner(PrivateKey privateKey){
        String algorithm= privateKey.getAlgorithm();
        if (privateKey instanceof RSAPrivateKey || Objs.equals("RSA",algorithm) ||Objs.equals("EC",algorithm)  ) {
            // Will also allow "RSASSA-PSS" alg RSAPrivateKey instances with MGF1ParameterSpec
            this.privateKey = privateKey;
        } else {
            throw new IllegalArgumentException("The private key algorithm must be RSA");
        }
    }

    @Override
    public void sign(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        Supplier<PrivateKey, Signature> signerSupplier = jwtAlgorithmToPKI.get(jwtSignAlgorithm);

        if(signerSupplier==null){
            throw new IllegalJWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data=(token.getHeader().toBase64UrlEncoded()+"."+token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature= Signatures.sign(signerSupplier.get(privateKey), data);

        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(PKISigner.jwtAlgorithmToPKI.keySet());
    }
}
