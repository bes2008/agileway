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
        Supplier<PrivateKey, Signature> signerSupplier = Signs.JWT_PKI_ALGORITHM_SIGNER_SUPPLIER.get(jwtSignAlgorithm);

        if (signerSupplier == null) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        byte[] signature = Signatures.sign(signerSupplier.get(privateKey), data);

        token.setSignature(Base64.encodeBase64URLSafeString(signature));
    }

    @Override
    public List<String> supportedAlgorithms() {
        return Lists.newArrayList(Signs.JWT_PKI_ALGORITHM_SIGNER_SUPPLIER.keySet());
    }
}
