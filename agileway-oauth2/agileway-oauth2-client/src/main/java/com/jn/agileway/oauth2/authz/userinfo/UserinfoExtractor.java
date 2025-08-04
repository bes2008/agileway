package com.jn.agileway.oauth2.authz.userinfo;

public interface UserinfoExtractor<T, U extends Userinfo> {
    U extract(T obj);
}
