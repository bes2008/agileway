package com.jn.agileway.jwt;

public interface Header extends ClaimSet{

    String getType();

    void setType(String type);

    String getAlgorithm();

    void setAlgorithm(String algorithm);

}
