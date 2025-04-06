package com.jn.agileway.http.authc.digest;

import com.jn.langx.util.Strings;
import com.jn.langx.util.enums.base.CommonEnum;

public enum DigestAlgorithm implements CommonEnum {
    SHA2_256("SHA2-256", false),
    SHA2_256_SESSION("SHA2-256", true),
    SHA2_512_256("SHA2-512/256", false),
    SHA2_512_256_SESSION("SHA2-512/256", true),
    MD5("MD5", false),
    MD5_SESSION("MD5", true);

    DigestAlgorithm(String algorithm, boolean session) {
        algorithm = Strings.upperCase(algorithm);
        this.algorithm = session ? (algorithm + "-sess") : algorithm;
    }

    private String algorithm;


    @Override
    public int getCode() {
        return ordinal();
    }

    @Override
    public String getDisplayText() {
        return getName();
    }

    @Override
    public String getName() {
        return this.algorithm;
    }
}
