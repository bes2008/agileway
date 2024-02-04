package com.jn.agileway.jwt.sign;

import com.jn.agileway.jwt.JWTException;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Signer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.List;
import java.util.Map;

public class PKISigner implements Signer {
    private static final Logger logger = Loggers.getLogger(PKISigner.class);
    private static Map<String, Supplier<PrivateKey, Signature>> jwtAlgorithmToSignerSupplier;

    static {
        Map<String, Supplier<PrivateKey, Signature>> map = Maps.newLinkedHashMap();
        // RSA 相关
        map.put("RS256", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withRSA", null, privateKey, null);
            }
        });
        map.put("RS384", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA384withRSA", null, privateKey, null);
            }
        });
        map.put("RS512", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA512withRSA", null, privateKey, null);
            }
        });

        map.put("PS256", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        map.put("PS384", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        map.put("PS512", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        // EC 相关
        map.put("ES256", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put("ES256K", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put("ES384", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA384withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put("ES512", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA512withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });

        map.put("EdDSA", new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                Provider bc = Security.getProvider("BC");
                if (bc == null) {
                    bc = new BouncyCastleProvider();
                    Securitys.addProvider(bc);
                }
                return Signatures.createSignature("ED25519", "BC", privateKey, Securitys.getSecureRandom());
            }
        });
        jwtAlgorithmToSignerSupplier = map;
    }

    private final PrivateKey privateKey;

    public PKISigner(PrivateKey privateKey) {
        String algorithm = privateKey.getAlgorithm();
        if (privateKey instanceof RSAPrivateKey || Objs.equals("RSA", algorithm)
                || privateKey instanceof ECPrivateKey || Objs.equals("EC", algorithm)
                || Objs.equals("ED25519", algorithm)
        ) {
            // Will also allow "RSASSA-PSS" alg RSAPrivateKey instances with MGF1ParameterSpec
            this.privateKey = privateKey;
        } else {
            throw new IllegalArgumentException("The private key algorithm must be RSA or ECDSA or ED25519");
        }
    }

    @Override
    public void sign(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        Supplier<PrivateKey, Signature> signerSupplier = jwtAlgorithmToSignerSupplier.get(jwtSignAlgorithm);

        if (signerSupplier == null) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature = Signatures.sign(signerSupplier.get(privateKey), data);

        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(PKISigner.jwtAlgorithmToSignerSupplier.keySet());
    }
}
