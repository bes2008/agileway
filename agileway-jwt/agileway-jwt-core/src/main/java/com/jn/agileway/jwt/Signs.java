package com.jn.agileway.jwt;

import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.cipher.AlgorithmParameterSupplier;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Supplier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.List;
import java.util.Map;

class Signs {
    static Map<String, String> JWT_TO_HMAC_ALGORITHMS;

    static {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put(JWTs.JWSAlgorithms.HS256, "hmacsha256");
        map.put(JWTs.JWSAlgorithms.HS384, "hmacsha384");
        map.put(JWTs.JWSAlgorithms.HS512, "hmacsha512");
        JWT_TO_HMAC_ALGORITHMS = map;
    }

    static Map<String, Supplier<PrivateKey, Signature>> JWT_PKI_ALGORITHM_SIGNER_SUPPLIER;
    static Map<String, Supplier<PublicKey, Signature>> JWT_PKI_ALGORITHM_VERIFIER_SUPPLIER;

    static {
        Map<String, Supplier<PrivateKey, Signature>> map = Maps.newLinkedHashMap();
        // RSA 相关
        map.put(JWTs.JWSAlgorithms.RS256, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withRSA", null, privateKey, null);
            }
        });
        map.put(JWTs.JWSAlgorithms.RS384, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA384withRSA", null, privateKey, null);
            }
        });
        map.put(JWTs.JWSAlgorithms.RS512, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA512withRSA", null, privateKey, null);
            }
        });

        map.put(JWTs.JWSAlgorithms.PS256, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        map.put(JWTs.JWSAlgorithms.PS384, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        map.put(JWTs.JWSAlgorithms.PS512, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
                return Signatures.createSignature("RSASSA-PSS", null, privateKey, null, parameterSpec);
            }
        });
        // EC 相关
        map.put(JWTs.JWSAlgorithms.ES256, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put(JWTs.JWSAlgorithms.ES256K, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA256withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put(JWTs.JWSAlgorithms.ES384, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA384withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });
        map.put(JWTs.JWSAlgorithms.ES512, new Supplier<PrivateKey, Signature>() {
            @Override
            public Signature get(PrivateKey privateKey) {
                return Signatures.createSignature("SHA512withECDSA", null, privateKey, Securitys.getSecureRandom());
            }
        });

        map.put(JWTs.JWSAlgorithms.EdDSA, new Supplier<PrivateKey, Signature>() {
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
        JWT_PKI_ALGORITHM_SIGNER_SUPPLIER = map;
    }


    static {
        Map<String, Supplier<PublicKey, Signature>> map = Maps.newLinkedHashMap();
        // RSA 相关
        map.put("RS256", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA256withRSA", null, publicKey, (AlgorithmParameterSupplier) null);
            }
        });
        map.put("RS384", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA384withRSA", null, publicKey, (AlgorithmParameterSupplier)null);
            }
        });
        map.put("RS512", new Supplier<PublicKey, Signature>() {
            @Override
            public Signature get(PublicKey publicKey) {
                return Signatures.createSignature("SHA512withRSA", null, publicKey, (AlgorithmParameterSupplier)null);
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
                final AlgorithmParameterSpec parameterSpec = new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1);
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
        JWT_PKI_ALGORITHM_VERIFIER_SUPPLIER = map;
    }

    public static List<String> supportedJWTHMacAlgorithms() {
        return Lists.newArrayList(JWT_TO_HMAC_ALGORITHMS.keySet());
    }

    public static List<String> supportedJWTPKIAlgorithms() {
        return Lists.newArrayList(JWT_PKI_ALGORITHM_SIGNER_SUPPLIER.keySet());
    }

    public static List<String> supportedJWTSignAlgorithms() {
        return Pipeline.<String>of(supportedJWTHMacAlgorithms())
                .addAll(supportedJWTPKIAlgorithms())
                .asList();
    }

    public static String getJcaHMacAlgorithm(String jwsHmacAlgorithm){
        return JWT_TO_HMAC_ALGORITHMS.get(Strings.upperCase(jwsHmacAlgorithm));
    }

}
