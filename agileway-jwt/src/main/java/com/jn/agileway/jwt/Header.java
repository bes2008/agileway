package com.jn.agileway.jwt;

public interface Header extends ClaimSet{

    String getType();

    String getAlgorithm();

}
