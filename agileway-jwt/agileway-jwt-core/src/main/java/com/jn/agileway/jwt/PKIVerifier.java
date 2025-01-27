package com.jn.agileway.jwt;

import com.jn.agileway.jwt.ec.ECDSA;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.crypto.signature.Signatures;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.io.Charsets;

import java.security.PublicKey;
import java.security.Signature;

public class PKIVerifier implements Verifier {

    private PublicKey publicKey;

    public PKIVerifier(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(JWSToken token) {
        String jwtSignAlgorithm = token.getHeader().getAlgorithm();
        Supplier<PublicKey, Signature> verifierSupplier = Signs.JWT_PKI_ALGORITHM_VERIFIER_SUPPLIER.get(jwtSignAlgorithm);

        if (verifierSupplier == null) {
            throw new JWTException(StringTemplates.formatWithPlaceholder("invalid jwt sign token: unsupported algorithm: {}", jwtSignAlgorithm));
        }

        String signature = token.getSignature();
        byte[] signatureBytes = Base64.decodeBase64(signature);
        if(JWTs.JWSAlgorithms.isECDSA(jwtSignAlgorithm)){
            try {
                signatureBytes = ECDSA.transcodeSignatureToDER(signatureBytes);
            } catch (JWTException e) {
                return false;
            }
        }

        byte[] data = (token.getHeader().toBase64UrlEncoded() + "." + token.getPayload().toBase64UrlEncoded()).getBytes(Charsets.UTF_8);
        return Signatures.verify(verifierSupplier.get(this.publicKey), data, signatureBytes);
    }
}
