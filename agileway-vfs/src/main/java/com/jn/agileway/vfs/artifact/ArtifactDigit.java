package com.jn.agileway.vfs.artifact;

public class ArtifactDigit {
    private String algorithm;
    private String digit;
    public ArtifactDigit(){}
    public ArtifactDigit(String algorithm,String digit){
        setAlgorithm(algorithm);
        setDigit(digit);
    }
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getDigit() {
        return digit;
    }

    public void setDigit(String digit) {
        this.digit = digit;
    }
}
