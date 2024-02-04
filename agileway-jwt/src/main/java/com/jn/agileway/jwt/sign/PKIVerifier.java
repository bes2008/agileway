package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.Verifier;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.cipher.AlgorithmParameterSupplier;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Map;

public class PKIVerifier implements Verifier {
    private static Map<String, Supplier<PublicKey, Signature>> jwtAlgorithmToSignerSupplier;

    static {
        Map<String, Supplier<PublicKey, Signature>> map = Maps.newLinkedHashMap();
        // RSA 相关
        map.put("RS256", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA256withRSA", null, publicKey, null);
            }
        });
        map.put("RS384", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA384withRSA", null, publicKey, null);
            }
        });
        map.put("RS512", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA512withRSA", null, publicKey, null);
            }
        });

        map.put("PS256", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
               final AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1);
                return Signatures.createSignature("RSASSA-PSS", null, publicKey, new AlgorithmParameterSupplier() {
                    @Override
                    public Object get(Key key, String s, String s1, Provider provider, SecureRandom secureRandom) {
                        return parameterSpec;
                    }
                });
            }
        });
        map.put("PS384", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
               final AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1);
                return Signatures.createSignature("RSASSA-PSS", null, publicKey, new AlgorithmParameterSupplier() {
                    @Override
                    public Object get(Key key, String s, String s1, Provider provider, SecureRandom secureRandom) {
                        return parameterSpec;
                    }
                });
            }
        });
        map.put("PS512", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
             final    AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                return Signatures.createSignature("RSASSA-PSS", null, publicKey, new AlgorithmParameterSupplier() {
                    @Override
                    public Object get(Key key, String s, String s1, Provider provider, SecureRandom secureRandom) {
                        return parameterSpec;
                    }
                });
            }
        });
        // EC 相关
        map.put("ES256", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA256withECDSA", null, publicKey);
            }
        });
        map.put("ES256K", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA256withECDSA", null, publicKey);
            }
        });
        map.put("ES384", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA384withECDSA", null, publicKey);
            }
        });
        map.put("ES512", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA512withECDSA", null, publicKey);
            }
        });

        map.put("EdDSA", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                Provider bc = Security.getProvider("BC");
                if (bc == null) {
                    bc = new BouncyCastleProvider();
                    Securitys.addProvider(bc);
                }
                return Signatures.createSignature("ED25519", "BC", publicKey);
            }
        });
        jwtAlgorithmToSignerSupplier = map;
    }

    private PublicKey publicKey;

    public PKIVerifier(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(JWSToken token, String signature) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        Supplier<PublicKey, Signature> verifierSupplier = jwtAlgorithmToSignerSupplier.get(jwtSignAlgorithm);

        if (verifierSupplier == null) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        return Signatures.verify(verifierSupplier.get(this.publicKey), data, Base64.decodeBase64(signature));
    }
}
