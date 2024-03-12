package com.jn.agileway.jwt.tests.nacos_jwt;


import com.jn.easyjson.core.util.JSONs;

public class NacosJwtPayload {

    private String sub;

    private long exp = System.currentTimeMillis() / 1000L;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return JSONs.toJson(this);
    }
}