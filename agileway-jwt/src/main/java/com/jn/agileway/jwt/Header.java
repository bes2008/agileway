package com.jn.agileway.jwt;

public interface Header extends ClaimSet{

    public String getType();

    public void setType(String type);

    public String getAlgorithm();

    public void setAlgorithm(String algorithm);

}
