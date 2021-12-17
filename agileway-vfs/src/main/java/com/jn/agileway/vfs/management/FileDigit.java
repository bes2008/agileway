package com.jn.agileway.vfs.management;

public class FileDigit {
    private String algorithm;
    private String digit;

    public FileDigit() {
    }

    public FileDigit(String algorithm, String digit) {
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
