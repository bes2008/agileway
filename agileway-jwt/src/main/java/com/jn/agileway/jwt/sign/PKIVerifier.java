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

    private PublicKey publicKey;

    public PKIVerifier(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(JWSToken token, String signature) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        Supplier<PublicKey, Signature> verifierSupplier = Signs.JWT_PKI_ALGORITHM_VERIFIER_SUPPLIER.get(jwtSignAlgorithm);

        if (verifierSupplier == null) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        return Signatures.verify(verifierSupplier.get(this.publicKey), data, Base64.decodeBase64(signature));
    }
}
